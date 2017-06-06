package com.example.a15017484.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.a15017484.p07_smsretriever.R.id.etNumber;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWord extends Fragment {
    Button btnRetrieve;
    TextView tvSms;
    EditText etWord;

    public FragmentWord() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        tvSms = (TextView) view.findViewById(R.id.tvSMS2);
        btnRetrieve = (Button) view.findViewById(R.id.btnRetrieve2);
        etWord = (EditText) view.findViewById(R.id.etWord);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }
                // Create all messages URI
                Uri uri = Uri.parse("content://sms");
                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();
                // Fetch SMS Message from Built-in Content Provider
                // The filter String


                // The matches for the ?
                String word = etWord.getText().toString();
                if (word.isEmpty()) {
                    return;
                }
                Log.d("word", word);
                String[] splitStr = word.split("\\s+");
                //"\\s" is a regular expression for white space characters and  "+" is for any string that contains it as split only takes in regular expressions.
                Log.d("check", splitStr[0]);
                int count = splitStr.length;
                String[] filterArgs = new String[count];
                String filter = "body LIKE ? ";
                for (int i = 0; i < count; i++) {
                    filterArgs[i] = "%" + splitStr[i] + "%";
                    Log.d("test", splitStr[i]);
                }
                //Scenario 3: if find smses that contain any of the words, change "AND" to "OR".
                //"AND" is to find smses that contain all the words.
                for (int x = 0; x < count - 1; x++) {
                    filter += "AND body LIKE ? ";
                }

                Log.d("filter", filter);
                // Fetch SMS Message from Built-in Content Provider

                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                String smsBody = "";

                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvSms.setText(smsBody);
            }
        });

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
