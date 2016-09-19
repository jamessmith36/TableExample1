package com.tae.james.tableexample1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tae.james.tableexample1.api.observerable.DataManagement;
import com.tae.james.tableexample1.connectionmanagement.ConnectionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

public class MainActivity extends AppCompatActivity implements UpdateActivity, NoDataResponse{

    private static final String TAG = MainActivity.class.getName();;
    private TableView<String[]> tableView;
    private JSONArray table2Data;
    private String[] tmp = null;
    private Intent intent;
    private boolean switchScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ConnectionManager(this);
        new DataManagement(this);
        tableView = (TableView<String[]>) findViewById(R.id.tableView);
        tableView.setColumnCount(2);
        int colorEvenRows = getResources().getColor(R.color.colorAccent);
        int colorOddRows = getResources().getColor(R.color.colorPrimaryDark);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows,
                colorOddRows));
        String[] headers = new String[]{"Forename", "Surname"};
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, headers));

       tableView.addDataClickListener(new DataClickListener());

            intent = new Intent(this, DataManagement.class);
            intent.putExtra("serveData", "serveData");
            startService(intent);


        if(switchScreen) {
            intent = new Intent(this, PeronalDataTable.class);
            intent.putExtra("tableData2", table2Data.toString());
            startActivity(intent);
        }
    }

    private void loadTable(List<String[]> data){
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, data));
    }

    private void noDataMessage(){
        Toast.makeText(this, "Please connect to internet to retrieve data",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void update(JSONArray jsonArray) {

        try {
            if(jsonArray != null) {
                JSONObject jsonObject;
                Log.v(TAG, "jsonData: " + jsonArray);
                final List<String[]> tableData;
                table2Data = jsonArray;
                tableData = new ArrayList<>();
                for (int cell = 0; cell < jsonArray.length(); cell++) {
                    jsonObject = jsonArray.getJSONObject(cell);
                    tmp = new String[]{jsonObject.getString("forename"), jsonObject.getString("surname")};
                    tableData.add(tmp);
                }

                Log.v(TAG, "cell array size: " + tmp.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadTable(tableData);
                    }
                });

            }
        }catch(JSONException e){
            Log.v(TAG, e.toString());
        }
    }

    @Override
    public void noData(Boolean status) {
        if (!status){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noDataMessage();
                }
            });
            Log.v(TAG, "got no data at present");
        }
    }

    private class DataClickListener implements TableDataClickListener<String[]> {

        /**
         * This method is called when there was a click on a certain table data.
         *
         * @param rowIndex    The index of the row that has been clicked.
         * @param clickedData
         */
        @Override
        public void onDataClicked(int rowIndex, String[] clickedData) {
            Log.v(TAG, "rowIndex: "+rowIndex);
            Log.v(TAG, "clickedData: "+clickedData[rowIndex]);
            switchScreen = true;
        }
    }
}