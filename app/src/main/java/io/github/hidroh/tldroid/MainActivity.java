package io.github.hidroh.tldroid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.ResourceCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        mEditText = (AutoCompleteTextView) findViewById(R.id.edit_text);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return actionId == EditorInfo.IME_ACTION_SEARCH &&
                        search(v.getText().toString(), null);
            }
        });
        mEditText.setAdapter(new CursorAdapter(this));
        mEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text = ((TextView) view.findViewById(android.R.id.text1)).getText();
                CharSequence platform = ((TextView) view.findViewById(android.R.id.text2)).getText();
                mEditText.setText(text);
                mEditText.setSelection(text.length());
                search(text, platform);
            }
        });
    }

    private boolean search(CharSequence query, CharSequence platform) {
        if (TextUtils.isEmpty(query)) {
            return false;
        }
        startActivity(new Intent(this, CommandActivity.class)
                .putExtra(CommandActivity.EXTRA_QUERY, query)
                .putExtra(CommandActivity.EXTRA_PLATFORM, platform));
        return true;
    }

    private static class CursorAdapter extends ResourceCursorAdapter {

        public CursorAdapter(final Context context) {
            super(context, R.layout.dropdown_item, null, false);
            setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    String queryString = constraint != null ? constraint.toString() : "";
                    return context.getContentResolver()
                            .query(TldrProvider.URI_COMMAND,
                                    null,
                                    TldrProvider.CommandEntry.COLUMN_NAME + " LIKE ?",
                                    new String[]{"%" + queryString + "%"},
                                    null);
                }
            });
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((TextView) view.findViewById(android.R.id.text1))
                    .setText(cursor.getString(cursor.getColumnIndexOrThrow(
                            TldrProvider.CommandEntry.COLUMN_NAME)));
            ((TextView) view.findViewById(android.R.id.text2))
                    .setText(cursor.getString(cursor.getColumnIndexOrThrow(
                            TldrProvider.CommandEntry.COLUMN_PLATFORM)));
        }
    }
}
