package vn.anhkhoa.projectwebsitebantailieu.adapter;

import android.content.Context;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.library.foysaltech.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.R;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentImageDto;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderHolder> {
    private Context context;
    private List<DocumentImageDto> arrayList;

    public SliderAdapter(Context context, List<DocumentImageDto> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public SliderHolder onCreateViewHolder(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent,false);
        return new SliderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderHolder viewHolder, int position){
        DocumentImageDto documentImageDto = arrayList.get(position);
        Glide.with(context).load(documentImageDto.getDocImageUrl()).into(viewHolder.imageView);
    }

    @Override
    public int getCount(){
        return arrayList.size();
    }
    public class SliderHolder extends SliderViewAdapter.ViewHolder {
        private ImageView imageView;
        public SliderHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}
