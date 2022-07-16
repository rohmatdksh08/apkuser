package com.jatmika.e_complaintrangkasbitung.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jatmika.e_complaintrangkasbitung.Model.Komplain;
import com.jatmika.e_complaintrangkasbitung.R;
import com.uncopt.android.widget.text.justify.JustifiedTextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class RecyclerAdapterKomplain extends RecyclerView.Adapter<RecyclerAdapterKomplain.RecyclerViewHolder> {
    private Context mContext;
    private List<Komplain> pengaduans;
    private OnItemClickListener mListener;

    public RecyclerAdapterKomplain(Context context, List<Komplain> uploads) {
        mContext = context;
        pengaduans = uploads;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_komplain, parent, false);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.frombottom);
        v.startAnimation(animation);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Komplain currentKomplain = pengaduans.get(position);
        holder.namaTextView.setText(currentKomplain.getNama());
        holder.isiTextView.setText("''" +currentKomplain.getIsi()+ "''");
        String status = holder.setText(currentKomplain.getStatus());
        if (status.equals("Menunggu Diproses")){
            holder.imgStatus.setImageResource(R.drawable.status_menunggu);
            holder.statusTextView.setTextColor(mContext.getResources().getColor(R.color.greenColor));
            holder.statusTextView.setText(currentKomplain.getStatus());
        } else if (status.equals("Dalam Proses")){
            holder.imgStatus.setImageResource(R.drawable.status_proses);
            holder.statusTextView.setTextColor(mContext.getResources().getColor(R.color.yellowColor));
            holder.statusTextView.setText(currentKomplain.getStatus());
        } else {
            holder.imgStatus.setImageResource(R.drawable.status_selesai);
            holder.statusTextView.setTextColor(mContext.getResources().getColor(R.color.redColor));
            holder.statusTextView.setText(currentKomplain.getStatus());
        }
        holder.tanggalTextView.setText(currentKomplain.getTanggal());
        holder.lihatTextView.setText(currentKomplain.getJml_lihat());
        holder.sukaTextView.setText(currentKomplain.getJml_suka());
        String balasan = holder.setText(currentKomplain.getJml_balas());
        if (balasan.equals("0")){
            holder.balasTextView.setText("Belum ada balasan");
        } else {
            holder.balasTextView.setText(currentKomplain.getJml_balas()+" Balasan");
        }
        String foto = holder.setText(currentKomplain.getFoto());
        if (foto.equals("")){
            holder.fotoImageView.setImageResource(R.drawable.placeholder3);
        } else {
            Glide.with(mContext)
                    .load(currentKomplain.getFoto())
                    .into(holder.fotoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return pengaduans.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView namaTextView, statusTextView, tanggalTextView, lihatTextView, sukaTextView, balasTextView;
        JustifiedTextView isiTextView;
        ImageView fotoImageView, imgStatus;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            namaTextView = itemView.findViewById(R.id.tvNama);
            isiTextView = itemView.findViewById(R.id.tvIsi);
            statusTextView = itemView.findViewById(R.id.tvStatus);
            tanggalTextView = itemView.findViewById(R.id.tvTanggal);
            lihatTextView = itemView.findViewById(R.id.tvLihat);
            sukaTextView = itemView.findViewById(R.id.tvSuka);
            balasTextView = itemView.findViewById(R.id.tvBalas);
            fotoImageView = itemView.findViewById(R.id.fotoImageView);
            imgStatus = itemView.findViewById(R.id.imgStatus);

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

        public String setText(String jml_balas) {
            return jml_balas;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

