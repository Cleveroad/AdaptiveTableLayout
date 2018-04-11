package com.cleveroad.adaptivetablelayout;

import android.os.Bundle;
import android.support.annotation.NonNull;

interface DataAdaptiveTableLayoutAdapter<VH extends ViewHolder> extends AdaptiveTableAdapter<VH>, ModificationsHolder {

    void onSaveInstanceState(@NonNull Bundle bundle);

    void onRestoreInstanceState(@NonNull Bundle bundle);

}
