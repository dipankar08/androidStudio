package in.co.dipankar.samplemvp.mainview.view;

import android.os.Bundle;

import in.co.dipankar.samplemvp.R;
import in.co.dipankar.samplemvp.mainview.model.IItemProvider;
import in.co.dipankar.samplemvp.mainview.model.ItemProvider;
import in.co.dipankar.samplemvp.mainview.presenter.IMainPresenter;
import in.co.dipankar.samplemvp.mainview.presenter.MainPresenter;


import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import java.util.List;

public class MainActivity extends Activity implements IMainView, AdapterView.OnItemClickListener {

    private ListView listView;
    private ProgressBar progressBar;
    private IMainPresenter presenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        presenter = new MainPresenter(this, new ItemProvider() {
        });
    }

    @Override protected void onResume() {
        super.onResume();
        presenter.onAttach();
    }

    @Override protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
    }

    @Override public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    @Override public void setItems(List<String> items) {
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
    }

    @Override public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemClicked(position);
    }
}