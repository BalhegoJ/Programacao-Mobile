package com.example.atividadem2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class CardapioDAO extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "cardapio.db";
    private static final int VERSAO = 2;

    public CardapioDAO(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE itens (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "preco TEXT," +
                "urlImagem TEXT," +
                "caminhoFotoLocal TEXT," +
                "categoria TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS itens");
        onCreate(db);
    }

    public void inserir(ItemCardapio item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nome", item.getNome());
        values.put("preco", item.getPreco());
        values.put("urlImagem", item.getUrlImagem());
        values.put("caminhoFotoLocal", item.getCaminhoFotoLocal());
        values.put("categoria", item.getCategoria());

        db.insert("itens", null, values);
    }

    public ArrayList<ItemCardapio> listarTudo() {
        ArrayList<ItemCardapio> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM itens ORDER BY categoria ASC, nome ASC";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                ItemCardapio item = new ItemCardapio();
                item.setNome(cursor.getString(cursor.getColumnIndexOrThrow("nome")));
                item.setPreco(cursor.getString(cursor.getColumnIndexOrThrow("preco")));
                item.setUrlImagem(cursor.getString(cursor.getColumnIndexOrThrow("urlImagem")));
                item.setCaminhoFotoLocal(cursor.getString(cursor.getColumnIndexOrThrow("caminhoFotoLocal")));
                item.setCategoria(cursor.getString(cursor.getColumnIndexOrThrow("categoria")));

                lista.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void limparTabela() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM itens");
    }
}