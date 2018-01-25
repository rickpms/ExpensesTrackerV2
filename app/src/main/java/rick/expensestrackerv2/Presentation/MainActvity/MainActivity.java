package rick.expensestrackerv2.Presentation.MainActvity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rick.expensestrackerv2.R;

//TODO ADD PROGRESS BAR on recyclerview
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    MainActivityPresenter presenter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.addBill)
    Button addBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        presenter = new MainActivityPresenter(recyclerView, this);

        addBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenter.addNewBill(MainActivity.this);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.saveBills(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.saveBills(this);
    }
}
