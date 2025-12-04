package com.vigor.checadorpreciossai;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.LastOwnerException;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class ProductListAdapter extends BaseAdapter {

    private Context context;
    private JSONArray jsonArray;
    private TextView txtL1;
    private TextView txtL2;
    private TextView txtL3;
    private TextView txtL4;
    private TextView txtL5;
    private TextView txtL6;
    private TextView txtListaCol;
    private TextView ctoEnt;
    private  DecimalFormat df = new DecimalFormat("0.00");




    public ProductListAdapter(Context context, JSONArray jsonArray){
        this.context = context;
        this.jsonArray = jsonArray;
    }


    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public JSONObject getItem(int position) {

        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context,R.layout.row_item_product,null);
        TextView txtCve_prod = (TextView)v.findViewById(R.id.txtCve_prod);
        TextView txtDescripcion = (TextView)v.findViewById(R.id.txtDescripcion);
        TextView txtActivo = (TextView)v.findViewById(R.id.txtActivo);


         txtL1 = (TextView)v.findViewById(R.id.txtL1);
         txtL2 = (TextView)v.findViewById(R.id.txtL2);
         txtL3 = (TextView)v.findViewById(R.id.txtL3);
         txtL4 = (TextView)v.findViewById(R.id.txtL4);
         txtL5 = (TextView)v.findViewById(R.id.txtL5);
         txtListaCol = (TextView)v.findViewById(R.id.txtListaCol);

         ctoEnt = (TextView)v.findViewById(R.id.ctoEnt);
         ctoEnt.setVisibility(View.INVISIBLE);
        JSONObject jsonObject;

        try {

            jsonObject = jsonArray.getJSONObject(position);
            txtCve_prod.setText(jsonObject.getString("cve_prod").trim());
            txtDescripcion.setText(jsonObject.getString("desc_prod").trim());
            txtActivo.setText(jsonObject.getString("activo").trim());

           hidePricesBySuc(jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return v;
    }

     private void hidePricesBySuc(JSONObject jsonObject) throws JSONException {
         Log.d("AQUI", "ENTRO AQUI"+ Util.SUC_NAME);
         switch (Util.SUC_NAME){
            case "COL":
                txtListaCol.setText("PRECIO COL: $"+jsonObject.getString("precio_con_descuento")+" "+jsonObject.getString("moneda"));
                break;
            case "TEC":
                txtL3.setText("PRECIO TEC: $"+jsonObject.getString("precio_con_descuento")+""+jsonObject.getString("moneda"));
                break;
            case "PIN":
                txtL1.setText("PRECIO PINO: $"+jsonObject.getString("precio_con_descuento")+" "+jsonObject.getString("moneda"));
                break;
            case "CIH":
                txtL2.setText("PRECIO CIHUA: $"+ jsonObject.getString("precio_con_descuento")+" "+jsonObject.getString("moneda"));
                break;
            case "GZM":
                //sayula usa la misma lista que guzman a si que creamos solo una variable alterna lista precios papra saber que ambas comparten la misma lista de precios
                if(Util._LISTA_PRECIOS == 4){
                    txtL6.setText("PRECIO SAYULA: $"+jsonObject.getString("precio_con_descuento")+" "+jsonObject.getString("moneda"));
                }else{
                    txtL4.setText("PRECIO GUZMAN: $" + jsonObject.getString("precio_con_descuento") + " " + jsonObject.getString("moneda"));
                }

                break;
            case "JOC":
                txtL5.setText("PRECIO JOCO: $"+jsonObject.getString("precio_con_descuento")+" "+jsonObject.getString("moneda"));
                 break;
            case "ADMIN":
                Log.d("sucursal", Util.SUC_NAME);
                ctoEnt.setVisibility(View.VISIBLE);
                ctoEnt.setText("PC: $"+jsonObject.getString("cto_ent")+" - IEPS: %"+jsonObject.getString("porcenieps"));
                txtL3.setText("TEC-COAH L3: $"+jsonObject.getString("l_3")+" "+jsonObject.getString("moneda"));
                txtListaCol.setText("COL L6: $"+df.format(Double.parseDouble(jsonObject.getString("l_6")))+" "+jsonObject.getString("moneda"));
                txtL2.setText("CIHUA. L2: $"+ jsonObject.getString("l_2")+" "+jsonObject.getString("moneda"));
                txtL4.setText("GUZ-SAYULA L4: $"+df.format(Double.parseDouble(jsonObject.getString("l_4")))+" "+jsonObject.getString("moneda"));
                txtL5.setText("JOC L5: $"+df.format(Double.parseDouble(jsonObject.getString("l_5")))+" "+jsonObject.getString("moneda"));
                txtL1.setText("P.SUAREZ L1: $"+jsonObject.getString("l_1")+" "+jsonObject.getString("moneda"));

                break;

        }

     }

     //aplica descuento del 10% al los precios de l_4
     public String aplicarDescuento(String precio){

        double descuento = Double.parseDouble(precio) * 0.1;
        String precio_con_descuento = String.valueOf((df.format(Double.parseDouble(precio) - descuento)));
        return precio_con_descuento;

     }


}
