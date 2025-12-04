package com.vigor.checadorpreciossai;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.layout_menu, menu);
            MenuItem mItem = menu.findItem(R.id.vClientProd);
            MenuItem mItemVentasClase = menu.findItem(R.id.vxclase);
            MenuItem mItemSaldoClientes = menu.findItem(R.id.MenuItemSaldoClientes);
            MenuItem productosMasVendidosPorAgente = menu.findItem(R.id.productosMasVendidosPorAgente);

            if (Util.IS_ADMIN!=1){
                mItem.setVisible(false);
                mItemVentasClase.setVisible(false);
                mItemSaldoClientes.setVisible(false);
                productosMasVendidosPorAgente.setVisible(false);
            }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cerrarSesion) {
            finishAffinity();
            return true;
        } else if (id == R.id.vClientProd) {
            startActivity(new Intent(this, VentasNetas_xcdp.class));
            return true;
        } else if (id == R.id.vxclase) {
            startActivity(new Intent(this, VnetasAlmacenClase.class));
            return true;
        } else if (id == R.id.MenuItemSaldoClientes) {
            startActivity(new Intent(this, ConsultarSaldoClientes.class));
            return true;
        } else if (id == R.id.productosMasVendidosPorAgente) {
            startActivity(new Intent(this, ProductosMasVendidosPorAgente.class));
            return true;
        } else if (id == R.id.MenuItemCotizadorFormulas) {
            startActivity(new Intent(this, CotizadorFormulasActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
