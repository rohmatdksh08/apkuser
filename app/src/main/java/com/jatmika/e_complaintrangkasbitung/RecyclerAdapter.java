package com.jatmika.e_complaintrangkasbitung;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jatmika.e_complaintrangkasbitung.Model.DataBerita;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public  class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{
    private Context mContext;
    private List<DataBerita> beritas;
    private OnItemClickListener mListener;

    public RecyclerAdapter(Context context, List<DataBerita> uploads) {
        mContext = context;
        beritas = uploads;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_berita, parent, false);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.frombottom);
        v.startAnimation(animation);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        DataBerita currentBerita = beritas.get(position);
        holder.judulTextView.setText(currentBerita.getjudul());
        holder.tanggalTextView.setText(currentBerita.getcreated_at());
        holder.penulisTextView.setText(currentBerita.getnama());
        holder.isiTextView.setText(currentBerita.getisi());
        String urlImage = "http://192.168.126.94:8000/uploads/"+currentBerita.getFoto();
        Glide.with(mContext)
                .load(urlImage)
                .into(holder.fotoImageView);
    }

    @Override
    public int getItemCount() {
        return beritas.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView judulTextView, tanggalTextView, penulisTextView, isiTextView;
        public ImageView fotoImageView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            judulTextView = itemView.findViewById ( R.id.judulTextView );
            tanggalTextView = itemView.findViewById(R.id.tanggalTextView);
            penulisTextView = itemView.findViewById(R.id.penulisTextView);
            isiTextView = itemView.findViewById(R.id.isiTextView);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
