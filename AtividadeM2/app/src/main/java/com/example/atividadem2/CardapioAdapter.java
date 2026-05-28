package com.example.atividadem2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class CardapioAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemCardapio> lista;
    private boolean ehOffline;

    public CardapioAdapter(Context context, ArrayList<ItemCardapio> lista, boolean ehOffline) {
        this.context = context;
        this.lista = lista;
        this.ehOffline = ehOffline;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lista_cardapio, parent, false);
        }

        ItemCardapio item = lista.get(position);

        LinearLayout layoutHeader = convertView.findViewById(R.id.layoutHeader);
        TextView tvCategoria = convertView.findViewById(R.id.textViewNomeCategoria);
        TextView tvNome = convertView.findViewById(R.id.textViewNomePrato);
        TextView tvPreco = convertView.findViewById(R.id.textViewPrecoPrato);
        ImageView imgPrato = convertView.findViewById(R.id.imageViewFotoPrato);

        String categoriaAtual = item.getCategoria() != null ? item.getCategoria() : "Outros";

        if (position == 0) {
            layoutHeader.setVisibility(View.VISIBLE);
            tvCategoria.setText(categoriaAtual.toUpperCase());
        } else {
            ItemCardapio itemAnterior = lista.get(position - 1);
            String categoriaAnterior = itemAnterior.getCategoria() != null ? itemAnterior.getCategoria() : "Outros";

            if (!categoriaAtual.equals(categoriaAnterior)) {
                layoutHeader.setVisibility(View.VISIBLE);
                tvCategoria.setText(categoriaAtual.toUpperCase());
            } else {
                layoutHeader.setVisibility(View.GONE);
            }
        }

        tvNome.setText(item.getNome());

        if (ehOffline) {
            tvPreco.setText("A consultar");
        } else {
            try {
                double valor = Double.parseDouble(item.getPreco());
                tvPreco.setText(String.format("R$ %.2f", valor));
            } catch (Exception e) {
                tvPreco.setText("R$ " + item.getPreco());
            }
        }

        imgPrato.setImageResource(android.R.drawable.ic_menu_gallery);

        if (item.getCaminhoFotoLocal() != null) {
            File imgFile = new File(item.getCaminhoFotoLocal());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (myBitmap != null) {
                    imgPrato.setImageBitmap(myBitmap);
                }
            }
        }

        return convertView;
    }
}