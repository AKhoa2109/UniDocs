    package vn.anhkhoa.projectwebsitebantailieu.fragment;

    import android.app.DownloadManager;
    import android.content.Context;
    import android.content.pm.PackageManager;
    import android.net.Uri;
    import android.os.AsyncTask;
    import android.os.Bundle;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.core.content.ContextCompat;
    import androidx.fragment.app.Fragment;

    import android.os.Environment;
    import android.os.ParcelFileDescriptor;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import com.shockwave.pdfium.PdfDocument;
    import com.shockwave.pdfium.PdfiumCore;

    import java.io.BufferedInputStream;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.InputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.time.LocalDateTime;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
    import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
    import vn.anhkhoa.projectwebsitebantailieu.database.NotificationDao;
    import vn.anhkhoa.projectwebsitebantailieu.databinding.FragmentPreviewBinding;
    import vn.anhkhoa.projectwebsitebantailieu.enums.NotificationType;
    import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
    import vn.anhkhoa.projectwebsitebantailieu.model.FileDocument;
    import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
    import vn.anhkhoa.projectwebsitebantailieu.utils.NotificationHelper;
    import vn.anhkhoa.projectwebsitebantailieu.utils.SessionManager;

    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link PreviewFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public class PreviewFragment extends Fragment {

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_FILE_DOCUMENT = "file";
        private static final String ARG_DOCUMENT = "document";

        FragmentPreviewBinding binding;
        private FileDocument fileDocument;
        private DocumentDto documentDto;
        private String directUrl;
        private SessionManager sessionManager;
        private final ActivityResultLauncher<String> requestWritePermission =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        startDownload();
                    } else {
                        Toast.makeText(getContext(), "Cần quyền ghi bộ nhớ để tải file", Toast.LENGTH_LONG).show();
                    }
                });


        public PreviewFragment() {
            // Required empty public constructor
        }

        // TODO: Rename and change types and number of parameters
        public static PreviewFragment newInstance(FileDocument fileDocument, DocumentDto documentDto) {
            PreviewFragment fragment = new PreviewFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_FILE_DOCUMENT, fileDocument);
            args.putSerializable(ARG_DOCUMENT,documentDto);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                fileDocument = (FileDocument) getArguments().getSerializable(ARG_FILE_DOCUMENT);
                documentDto = (DocumentDto) getArguments().getSerializable(ARG_DOCUMENT);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = FragmentPreviewBinding.inflate(inflater, container, false);
            sessionManager = SessionManager.getInstance(requireContext());
            String fileUrl = fileDocument.getFileUrl();
            String fileId = extractFileIdFromUrl(fileUrl);
            if (fileId == null) {
                Toast.makeText(getContext(), "Liên kết không hợp lệ", Toast.LENGTH_SHORT).show();
            }
            try {
                String rawUrl = "https://drive.google.com/uc?export=download&id=" + fileId;
                directUrl = rawUrl;
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi tạo URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("PREVIEW", "buildDirectUrl", e);
            }


            new DownloadPdfTask().execute(directUrl);

            binding.btnDownload.setOnClickListener(v -> {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestWritePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    startDownload();
                }
            });


            return binding.getRoot();
        }

        private void startDownload() {
            String fileName = fileDocument.getDocName().replaceAll("[/\\\\:*?\"<>|-]", "") + ".pdf";

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(directUrl))
                    .setTitle(fileName)
                    .setDescription("Đang tải xuống...")
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName.trim())
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);

            DownloadManager dm = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                long downloadId = dm.enqueue(request);
                Toast.makeText(getContext(), "Đang tải: " + fileName, Toast.LENGTH_LONG).show();

                // Debug tải file
                new Thread(() -> {
                    boolean downloading = true;
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    while (downloading) {
                        try (android.database.Cursor cursor = dm.query(query)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                                if (statusIndex != -1) {
                                    int status = cursor.getInt(statusIndex);
                                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                        downloading = false;
                                        Log.d("DOWNLOAD", "Tải thành công: " + fileName);
                                        sendLocalNotification(fileName);
                                        sendWSNotification(fileName);
                                    } else if (status == DownloadManager.STATUS_FAILED) {
                                        downloading = false;
                                        if (reasonIndex != -1) {
                                            int reason = cursor.getInt(reasonIndex);
                                            Log.e("DOWNLOAD", "Tải thất bại. Lý do: " + reason);
                                        } else {
                                            Log.e("DOWNLOAD", "Tải thất bại. Không thể lấy lý do.");
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("DOWNLOAD", "Lỗi kiểm tra trạng thái tải", e);
                            downloading = false;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Không khởi tạo được DownloadManager", Toast.LENGTH_LONG).show();
            }
        }

        private class DownloadPdfTask extends AsyncTask<String, Integer, File> {
            @Override
            protected void onPreExecute() {
                binding.progressBar.setIndeterminate(false);
                binding.progressBar.setProgress(0);
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected File doInBackground(String... urls) {
                String urlStr = urls[0];
                String fileName = fileDocument.getDocName() + ".pdf";
                File outputFile = new File(requireContext().getCacheDir(), fileName);
                try (InputStream in = new BufferedInputStream(new URL(urlStr).openStream());
                     FileOutputStream out = new FileOutputStream(outputFile)) {
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                    conn.connect();
                    int fileLength = conn.getContentLength();
                    byte[] data = new byte[4096];
                    int total = 0;
                    int count;
                    while ((count = in.read(data)) != -1) {
                        total += count;
                        if (fileLength > 0) {
                            publishProgress((total * 100) / fileLength);
                        }
                        out.write(data, 0, count);
                    }
                    return outputFile;
                } catch (Exception e) {
                    Log.e("Preview", "Download error", e);
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                binding.progressBar.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(File file) {
                binding.progressBar.setVisibility(View.GONE);
                if (file != null) {
                    loadPdf(file);
                } else {
                    Toast.makeText(getContext(), "Tải tài liệu để xem thất bại", Toast.LENGTH_LONG).show();
                }
            }
        }

        private void loadPdf(File file) {
            try {
                PdfiumCore pdfiumCore = new PdfiumCore(requireContext());
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                PdfDocument pdfDoc = pdfiumCore.newDocument(fd);
                int pageCount = pdfiumCore.getPageCount(pdfDoc);
                pdfiumCore.closeDocument(pdfDoc);

                int[] pagesToShow;
                if (pageCount > 5) {
                    pagesToShow = new int[]{0, 1, 2, 3, 4};
                } else {
                    pagesToShow = new int[]{0};
                }

                binding.pdfView.fromFile(file)
                        .pages(pagesToShow)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableAnnotationRendering(true)
                        .load();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Lỗi hiển thị PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Preview", "loadPdf", e);
            }
        }

        private String extractFileIdFromUrl(String url) {
            java.util.regex.Pattern p = java.util.regex.Pattern
                    .compile("/d/([a-zA-Z0-9_-]+)(/|\\?|$)");
            java.util.regex.Matcher m = p.matcher(url);
            return m.find() ? m.group(1) : null;
        }

        private void sendLocalNotification(String fileName){
            Long userId = sessionManager.getUser().getUserId();
            NotificationDto localNoti = new NotificationDto(null,userId,"Tải xuống thành công","Đã tải xuống: " + fileName, NotificationType.DOWNLOAD, LocalDateTime.now(),false);

            new Thread(() -> {
                NotificationDao dao = new NotificationDao(requireContext());
                dao.addNotification(localNoti);
            }).start();

            NotificationHelper.showNotification(
                    requireContext(),
                    NotificationHelper.Channel.DOWNLOAD,
                    "Tải xuống hoàn tất",
                    "Tài liệu " + fileName + " đã được tải"
            );
        }

        private void sendWSNotification(String fileName){
            NotificationDto wsNoti = new NotificationDto(null,documentDto.getUserId(),"Tải xuống được tải xuống","Người dùng " + sessionManager.getUser().getName()
                    + " đã tải file " + fileName, NotificationType.DOWNLOAD, LocalDateTime.now(),false);

            ApiService.apiService.pushWSNotification(wsNoti).enqueue(new Callback<ResponseData<Void>>() {
                @Override
                public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                    if (response.isSuccessful()) {
                        Log.e("WS", "Gửi thông báo thành công");
                    }
                    else{
                        Log.e("WS", "Gửi thông báo thất bại");
                    }
                }

                @Override
                public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                    Log.e("WS", "Lỗi kết nối", t);
                }
            });
        }

    }