package io.github.muntashirakon.AppManager.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import io.github.muntashirakon.AppManager.R;
import io.github.muntashirakon.AppManager.fragments.AppDetailsFragment;

public class AppDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_PACKAGE_NAME = "pkg";

    private String mPackageName;
    private TypedArray mTabTitleIds;
    AppDetailsFragmentStateAdapter appDetailsFragmentStateAdapter;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);
        mPackageName = getIntent().getStringExtra(AppInfoActivity.EXTRA_PACKAGE_NAME);
        if (mPackageName == null) {
            Toast.makeText(this, getString(R.string.empty_package_name), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Set title
        try {
            setTitle(getPackageManager().getApplicationInfo(mPackageName, 0).loadLabel(getPackageManager()).toString());
        } catch (PackageManager.NameNotFoundException ignored) {
            finish();
            return;
        }
        // Initialize tabs
        mTabTitleIds = getResources().obtainTypedArray(R.array.TAB_TITLES);
        FragmentManager fragmentManager = getSupportFragmentManager();
        appDetailsFragmentStateAdapter = new AppDetailsFragmentStateAdapter(fragmentManager, getLifecycle());
        viewPager2 = findViewById(R.id.pager);
        viewPager2.setAdapter(appDetailsFragmentStateAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager2, true,
                (tab, position) -> tab.setText(mTabTitleIds.getText(position))).attach();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_app_details_actions, menu);
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_app_info) {
            Intent appInfoIntent = new Intent(this, AppInfoActivity.class);
            appInfoIntent.putExtra(AppInfoActivity.EXTRA_PACKAGE_NAME, mPackageName);
            startActivity(appInfoIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    // For tab layout
    private class AppDetailsFragmentStateAdapter extends FragmentStateAdapter {
        AppDetailsFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new AppDetailsFragment(position);
        }

        @Override
        public int getItemCount() {
            return mTabTitleIds.length();
        }
    }
}
