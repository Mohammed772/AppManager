/*
 * Copyright (C) 2020 Muntashir Al-Islam
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.muntashirakon.AppManager.misc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import io.github.muntashirakon.AppManager.BaseActivity;
import io.github.muntashirakon.AppManager.R;
import io.github.muntashirakon.AppManager.batchops.BatchOpsManager;
import io.github.muntashirakon.AppManager.batchops.BatchOpsService;
import io.github.muntashirakon.AppManager.utils.PackageUtils;

public class AlertDialogActivity extends BaseActivity {
    @Override
    protected void onAuthenticated(@Nullable Bundle savedInstanceState) {
        if (getIntent() == null) {
            finish();
            return;
        }
        // Check for failed batch ops
        ArrayList<String> failedPackages = getIntent().getStringArrayListExtra(BatchOpsService.EXTRA_FAILED_PKG);
        ArrayList<Integer> userHandles = getIntent().getIntegerArrayListExtra(BatchOpsService.EXTRA_OP_USERS);
        String failureMessage = getIntent().getStringExtra(BatchOpsService.EXTRA_FAILURE_MESSAGE);
        int op = getIntent().getIntExtra(BatchOpsService.EXTRA_OP, BatchOpsManager.OP_NONE);
        Bundle args = getIntent().getBundleExtra(BatchOpsService.EXTRA_OP_EXTRA_ARGS);
        ArrayList<String> packageLabels = PackageUtils.packagesToAppLabels(getPackageManager(), failedPackages, userHandles);
        // Failed
        if (failedPackages != null) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(failureMessage)
                    .setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                            packageLabels), null)
                    .setNegativeButton(R.string.ok, null)
                    .setPositiveButton(R.string.try_again, (dialog, which) -> {
                        Intent intent = new Intent(this, BatchOpsService.class);
                        intent.putStringArrayListExtra(BatchOpsService.EXTRA_OP_PKG, failedPackages);
                        intent.putIntegerArrayListExtra(BatchOpsService.EXTRA_OP_USERS, userHandles);
                        intent.putExtra(BatchOpsService.EXTRA_OP, op);
                        intent.putExtra(BatchOpsService.EXTRA_OP_EXTRA_ARGS, args);
                        ContextCompat.startForegroundService(this, intent);
                    })
                    .setOnDismissListener(dialog -> finish())
                    .show();
        }
        getIntent().removeExtra(BatchOpsService.EXTRA_FAILED_PKG);
        getIntent().removeExtra(BatchOpsService.EXTRA_OP_USERS);
        getIntent().removeExtra(BatchOpsService.EXTRA_FAILURE_MESSAGE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
    }
}
