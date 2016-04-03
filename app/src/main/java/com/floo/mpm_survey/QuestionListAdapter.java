package com.floo.mpm_survey;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PathEffect;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dimas on 2/17/2016.
 */
public class QuestionListAdapter extends BaseAdapter {

    Context context;
    List<Question> questionList;
    private HashMap<String, Option> surveyAnswer;
    boolean isEditing = false;
    JSONArray prevAnswer;
    static Button lastClickedUploadButton;
    static TextView lastTextNeedChange;
    static ImageView lastImageNeedChange;
    LayoutInflater inflater;



    public HashMap<String, Option> getSurveyAnswer() {
        return surveyAnswer;
    }
    /*
    key is tag -> answerX ->>if checkboxes answerX_Y,option
    the value is the option
    if input text,number,or upload stored in option.text

     */

    public List<Question> getQuestionList() {
        return questionList;
    }

    public QuestionListAdapter(Context context,List<Question> questionList,boolean isEditing,JSONArray prevAnswer){
        this.context = context;
        this.questionList = questionList;
        surveyAnswer = new HashMap<>();
        this.isEditing = isEditing;
        if(isEditing){
            this.prevAnswer = prevAnswer;
            setEditingData();
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public int getItemViewType(int position) {
        int questionType = Integer.parseInt(questionList.get(position).getJenis_jawaban());
        int viewType=0;
        if(questionType==Question.CHOICE)
            viewType= Question.CHOICE;
        else if(questionType==Question.INPUT_NUMBER)
            viewType=Question.INPUT_NUMBER;
        else if(questionType==Question.INPUT_TEXT)
            viewType=Question.INPUT_TEXT;
        else if(questionType==Question.MULTIPLE)
            viewType=Question.MULTIPLE;
        else if(questionType==Question.UPLOAD)
            viewType=Question.UPLOAD;

        return viewType;

    }

    @Override
    public int getCount() {
        return questionList.size();
    }

    @Override
    public Object getItem(int position) {
        return questionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(questionList.get(position).getPertanyaan_id());
    }
    private int dpToPx(int dp){
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }

    private void setEditingData(){
        for(int i=0;i<prevAnswer.length();i++)
        {
            try {
                JSONObject obj = prevAnswer.getJSONObject(i);
                String questionID = obj.getString("PERTANYAAN_ID");
                int questionType = Integer.parseInt(obj.getString("JENIS_JAWABAN"));
                JSONArray options = obj.getJSONArray("OPTION");
                if(questionType==Question.CHOICE||questionType==Question.UPLOAD||
                        questionType==Question.INPUT_NUMBER||questionType==Question.INPUT_TEXT){
                    String tag = questionID+"**-";
                    Option option = new Option();
                    option.setTEXT(options.getJSONObject(0).getString("TEXT"));
                    option.setBOBOT(options.getJSONObject(0).getString("BOBOT"));
                    option.setURUTAN(options.getJSONObject(0).getString("URUTAN"));
                    option.setOPTION_ID(options.getJSONObject(0).getString("OPTION_ID"));
                    surveyAnswer.put(tag,option);
                }
                else if(questionType==Question.MULTIPLE) {
                    for (int j = 0; j < options.length(); j++) {
                        Option option = new Option();
                        option.setTEXT(options.getJSONObject(j).getString("TEXT"));
                        option.setBOBOT(options.getJSONObject(j).getString("BOBOT"));
                        option.setURUTAN(options.getJSONObject(j).getString("URUTAN"));
                        option.setOPTION_ID(options.getJSONObject(j).getString("OPTION_ID"));
                        String tag = questionID+"**"+options.getJSONObject(j).getString("OPTION_ID");
                        surveyAnswer.put(tag,option);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Option> searchAnswerBy(String questionID){
        List<Option> result = new ArrayList<>();
        for(int i=0;i<prevAnswer.length();i++)
        {
            try {
                JSONObject obj = prevAnswer.getJSONObject(i);
                if(obj.getString("PERTANYAAN_ID").equals(questionID)){
                    JSONArray options = obj.getJSONArray("OPTION");
                    for(int j=0;j<options.length();j++){
                        Option option = new Option();
                        option.setTEXT(options.getJSONObject(j).getString("TEXT"));
                        option.setBOBOT(options.getJSONObject(j).getString("BOBOT"));
                        option.setURUTAN(options.getJSONObject(j).getString("URUTAN"));
                        option.setOPTION_ID(options.getJSONObject(j).getString("OPTION_ID"));
                        result.add(option);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //// TODO: 31-Mar-16 fixing render problem 
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View v = convertView;
        boolean convertViewWasNull = false;
        int questionType = getItemViewType(position);
        RadioGroup radioGroup=null;
        EditText inputNumber=null;
        EditText inputText=null;
        Button chooseFile=null;
        TextView fileName=null;
        ImageView imageView = null;

        List<CheckBox> checkBoxes = new ArrayList<>();
        if (v == null) {
            convertViewWasNull = true;
            // Inflate the layout according to the view type
            v = inflater.inflate(R.layout.component_soal, parent, false);
            LinearLayout soalLayout = (LinearLayout)v.findViewById(R.id.soalLayout);
            LinearLayout.LayoutParams soalParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            soalParams.setMargins(dpToPx(21),dpToPx(5),dpToPx(35),dpToPx(2));
            if(questionType==Question.CHOICE){
                List<Option>options = questionList.get(position).getOPTIONS();
                RadioGroup.LayoutParams radioParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                radioGroup = (RadioGroup) inflater.inflate(R.layout.component_radiogroup,null);
                radioParams.setMargins(0, 0, 0, dpToPx(2));
                radioGroup.setId(Integer.parseInt(questionList.get(position).getPertanyaan_id()));
                for (int i = 0; i < options.size(); i++) {
                    RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.component_radiobutton,null);
                    radioButton.setId(Integer.parseInt(options.get(i).getOPTION_ID()));
                    radioButton.setTag(options.get(i));
                    radioButton.setText(options.get(i).getTEXT());
                    radioGroup.addView(radioButton,radioParams);
                }
                soalLayout.addView(radioGroup,soalParams);

            }
            else if(questionType==Question.INPUT_NUMBER) {
                inputNumber = (EditText)inflater.inflate(R.layout.component_inputnumber,null);
                inputNumber.setId(Integer.parseInt(questionList.get(position).getPertanyaan_id()));
                inputNumber.setPadding(dpToPx(3),dpToPx(3),dpToPx(3),dpToPx(3));
                soalLayout.addView(inputNumber,soalParams);
            }
            else if(questionType==Question.INPUT_TEXT) {
                inputText = (EditText)inflater.inflate(R.layout.component_inputtext,null);
                inputText.setId(Integer.parseInt(questionList.get(position).getPertanyaan_id()));
                inputText.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
                soalLayout.addView(inputText,soalParams);

            }
            else if(questionType==Question.MULTIPLE){
                List<Option>options = questionList.get(position).getOPTIONS();

                for(int i=0;i<options.size();i++){
                    CheckBox checkBox = (CheckBox)inflater.inflate(R.layout.component_checkbox,null);
                    checkBox.setId(Integer.parseInt(options.get(i).getOPTION_ID()));
                    checkBox.setTag(R.id.tagData,options.get(i));
                    checkBox.setText(options.get(i).getTEXT());
                    soalLayout.addView(checkBox, soalParams);
                    checkBoxes.add(checkBox);
                }

            }
            else if(questionType==Question.UPLOAD) {
                TableLayout tableLayout = (TableLayout)inflater.inflate(R.layout.component_upload,null);
                tableLayout.setPadding(0, 0, 0, dpToPx(5));
                soalLayout.addView(tableLayout, soalParams);
                chooseFile = (Button)v.findViewById(R.id.btn_Chose);
                fileName = (TextView)v.findViewById(R.id.txt_File);
                imageView = (ImageView)v.findViewById(R.id.imageViewer);
            }
        }
        if(questionType==Question.CHOICE){
            if(radioGroup!=null) {
                radioGroup.setTag(questionList.get(position).getPertanyaan_id() + "**-");
                List<Option>userAnswer;
                if(isEditing){
                    String initialTag = (String)radioGroup.getTag();
                    String questionID = initialTag.split(Pattern.quote("**"))[0];
                    userAnswer = searchAnswerBy(questionID);
                    if(userAnswer.size()>0)
                    {
                        int childCount = radioGroup.getChildCount();
                        for(int i =0;i<childCount;i++){
                            View o = radioGroup.getChildAt(i);
                            if (o instanceof RadioButton) {
                                RadioButton radioButton = (RadioButton) o;
                                Option tagOption = (Option) radioButton.getTag();
                                if (userAnswer.get(0).getOPTION_ID().equals(tagOption.getOPTION_ID())) {
                                    radioButton.setChecked(true);
                                }
                            }
                        }

                    }
                }
                if (convertViewWasNull)
                    radioGroup.setOnCheckedChangeListener(new RadioGroupChangedListener(radioGroup));

            }
        }

        else if(questionType==Question.INPUT_NUMBER) {
            if(inputNumber!=null) {
                inputNumber.setTag(questionList.get(position).getPertanyaan_id() + "**-");
                String tag = (String)inputNumber.getTag();
                String questionID = tag.split(Pattern.quote("**"))[0];
                if(isEditing){
                    List<Option>temp = searchAnswerBy(questionID);
                    if(temp.size()>0){
                        inputNumber.setText(temp.get(0).getTEXT(),TextView.BufferType.EDITABLE);
                    }
                }
                if (convertViewWasNull)
                    inputNumber.addTextChangedListener(new GenericTextWatcher(inputNumber));

            }
        }
        else if(questionType==Question.INPUT_TEXT) {
            if(inputText!=null) {
                inputText.setTag(questionList.get(position).getPertanyaan_id() + "**-");
                String tag = (String)inputText.getTag();
                String questionID = tag.split(Pattern.quote("**"))[0];
                if(isEditing){
                    List<Option>temp = searchAnswerBy(questionID);
                    if(temp.size()>0){
                        inputText.setText(temp.get(0).getTEXT(),TextView.BufferType.EDITABLE);
                    }
                }
                if (convertViewWasNull)
                    inputText.addTextChangedListener(new GenericTextWatcher(inputText));

            }
        }
        else if(questionType==Question.MULTIPLE){
            for(int i=0;i<checkBoxes.size();i++){
                CheckBox checkBox = checkBoxes.get(i);
                Option checkBoxData = (Option) checkBox.getTag(R.id.tagData);
                checkBox.setTag(R.id.tagName,questionList.get(position).getPertanyaan_id()+"**"+checkBoxData.getOPTION_ID());
                //this.view = (CompoundButton)view;
                List<Option>userAnswer;
                if(isEditing){
                    String initialTag = (String)checkBox.getTag(R.id.tagName);
                    String questionID = initialTag.split(Pattern.quote("**"))[0];
                    userAnswer = searchAnswerBy(questionID);
                    if(userAnswer.size()>0)
                    {
                        for(int j =0;j<userAnswer.size();j++){
                            Option tagOption = (Option)checkBox.getTag(R.id.tagData);
                            if (userAnswer.get(j).getOPTION_ID().equals(tagOption.getOPTION_ID())) {
                                checkBox.setChecked(true);
                            }
                        }

                    }
                }
                if(convertViewWasNull)
                    checkBox.setOnCheckedChangeListener(new CheckBoxCheckListener(checkBox));
            }
        }
        else if(questionType==Question.UPLOAD) {
            if(chooseFile!=null) {
                chooseFile.setTag(questionList.get(position).getPertanyaan_id() + "**-");
                String localTag = (String)chooseFile.getTag();

                String questionID = localTag.split(Pattern.quote("**"))[0];
                if(isEditing){
                    List<Option>tempOptions = searchAnswerBy(questionID);
                    if(tempOptions.size()>0){
                        String[] fullPath = tempOptions.get(0).getTEXT().split("/");
                        String tempFileName = fullPath[fullPath.length-1];
                        fileName.setText(tempFileName);
                        setImageViewFromPath(tempOptions.get(0).getTEXT(),imageView);
                    }
                }
                if (convertViewWasNull)
                    chooseFile.setOnClickListener(new ButtonClickListener(chooseFile, fileName,imageView));
            }
        }
        TextView pertanyaan = (TextView)v.findViewById(R.id.textPertanyaan);
        TextView nomorSoal = (TextView)v.findViewById(R.id.nomorSoal);
        LinearLayout soalLayout = (LinearLayout)v.findViewById(R.id.soalLayout);
        pertanyaan.setText(questionList.get(position).getPertanyaan());
        nomorSoal.setText((position+1)+"");
        if(!questionList.get(position).isAnswered())
            soalLayout.setBackgroundColor(ContextCompat.getColor(context,R.color.accent));
        else
            soalLayout.setBackgroundColor(Color.WHITE);
        return v;
    }
    private static class ViewHolder{
        TextView pertanyaan,nomorSoal,fileName;
        LinearLayout soalLayout;
        RadioGroup radioGroup;
        TableLayout tableLayout;
        Button chooseFile;
        ImageView imageView;
        List<CheckBox> checkBoxes = new ArrayList<>();
        EditText inputNumber,inputText;
    }/*
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        boolean convertViewWasNull = false;
        int questionType = getItemViewType(position);
        ViewHolder holder = null;
        if(convertView==null){
            convertViewWasNull = true;
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.component_soal,null);
            holder.nomorSoal = (TextView)convertView.findViewById(R.id.nomorSoal);
            holder.pertanyaan = (TextView)convertView.findViewById(R.id.textPertanyaan);
            holder.soalLayout = (LinearLayout)convertView.findViewById(R.id.soalLayout);
            if(questionType==Question.CHOICE){

            }
            else if(questionType==Question.MULTIPLE){

            }
            else if(questionType==Question.INPUT_TEXT){

            }
            else if(questionType==Question.INPUT_NUMBER){

            }
            else if(questionType==Question.UPLOAD){

            }

        }
        else {

        }

        return convertView;
    }*/
    Uri outputFileUri;
    private void openImageIntent() {
// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";//Utils.getUniqueImageFilename();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        ((Activity) context).startActivityForResult(chooserIntent, QuestionActivity.CHOOSE_IMAGE_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            Log.e("activity result",requestCode+"");
            if (requestCode == QuestionActivity.CHOOSE_IMAGE_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if(action!=null)
                        Log.e("cameran intent",action);
                    else
                        Log.e("cameran intent","null");

                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }
                Uri selectedImageUri;
                String filePath;
                if (isCamera) {
                    if(data!=null)
                        Log.e("data intent",data.getDataString());
                    selectedImageUri = outputFileUri;
                    filePath=selectedImageUri.toString();
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = context.getContentResolver().query(selectedImageUri,filePathColumn,null,null,null);
                    if(cursor!=null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();
                    }
                    else{
                        Log.e("activity result","cursor null: ");
                        filePath = selectedImageUri.getPath();
                    }
                }
                Log.e("activity result", "path: " + filePath);
                String buttonTag = (String)lastClickedUploadButton.getTag();
                Log.e("activity result", "buttonTag event " + buttonTag);
                String[]splitText = filePath.split("/");
                lastTextNeedChange.setText(splitText[splitText.length-1]);
                setImageViewFromPath(filePath,lastImageNeedChange);
                Option dummy = new Option();
                dummy.setTEXT(filePath);
                QuestionListAdapter.this.surveyAnswer.put(buttonTag, dummy);
            }

        }
    }

    private void setImageViewFromPath(String fullpath,ImageView imageView){
        Uri uri = Uri.parse(fullpath);
        File imgFile = new  File(uri.getPath());

        if(imgFile.exists()){
            Log.e("image file","exist");
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            for (options.inSampleSize = 1; options.inSampleSize <= 32; options.inSampleSize++) {
                try {
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                    Log.e("image handle", "Decoded successfully for sampleSize " + options.inSampleSize);
                    break;
                } catch (OutOfMemoryError outOfMemoryError) {
                    // If an OutOfMemoryError occurred, we continue with for loop and next inSampleSize value
                    Log.e("image handle", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize
                            + " retrying with higher value");
                }
            }
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);

        }
        else{
            imageView.setVisibility(View.GONE);
            Log.e("image file", "not exist");
        }

    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;
        String tag;


        private GenericTextWatcher(View view) {
            this.view = view;
            tag = (String) this.view.getTag();
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {

            String text = editable.toString();
            Option answer = new Option();
            if(!text.equals(""))
            {
                answer.setTEXT(text);
                //save the value for the given tag :
                QuestionListAdapter.this.surveyAnswer.put(tag, answer);
            }
            else{
                if(QuestionListAdapter.this.surveyAnswer.get(tag)!=null)
                    QuestionListAdapter.this.surveyAnswer.remove(tag);
            }

        }
    }
    private class RadioGroupChangedListener implements RadioGroup.OnCheckedChangeListener{

        private View view;
        private String tag;
        private Option answer = null;

        public RadioGroupChangedListener(View view) {
            this.view = view;
            //tag = (String)this.view.getTag();
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
            tag = (String)group.getTag();
            // This puts the value (true/false) into the variable
            boolean isChecked = checkedRadioButton.isChecked();
            // If the radiobutton that has changed in check state is now checked...
            if (isChecked)
            {
                answer = (Option)checkedRadioButton.getTag();
            }
            //save the value for the given tag :
            QuestionListAdapter.this.surveyAnswer.put(tag, answer);


        }
    }
    private class CheckBoxCheckListener implements CompoundButton.OnCheckedChangeListener{

        private View view;
        private String tag;
        private Option answer = null;
        public CheckBoxCheckListener(View view) {
            this.view = view;

            //this.tag = (String)this.view.getTag(R.id.tagName);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            this.tag = (String)buttonView.getTag(R.id.tagName);
            if (buttonView.isChecked()) {
                answer = (Option)buttonView.getTag(R.id.tagData);
                //save the value for the given tag :
                QuestionListAdapter.this.surveyAnswer.put(tag, answer);
            }
            else {
                if(QuestionListAdapter.this.surveyAnswer.get(tag)!=null)
                    QuestionListAdapter.this.surveyAnswer.remove(tag);
            }


        }
    }
    private class ButtonClickListener implements Button.OnClickListener{
        View view;
        TextView fileTextView;
        ImageView imageView;
        String tag;
        public ButtonClickListener(View view,TextView fileTextView,ImageView imageView) {
            this.view = view;
            //this.tag = (String)this.view.getTag();
            this.fileTextView = fileTextView;
            this.imageView = imageView;

        }

        @Override
        public void onClick(View v) {
            this.tag = (String)v.getTag();
            lastClickedUploadButton = (Button)v;
            lastTextNeedChange = fileTextView;
            lastImageNeedChange = imageView;
            openImageIntent();

        }
    }

}
