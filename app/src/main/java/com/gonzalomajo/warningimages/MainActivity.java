package com.gonzalomajo.warningimages;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Rect;


import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
	ImageView mImageToWarn;
	Uri mSelectedImage;
	String mFilePath;
	String mFileContentUriSaved;
	TextView mTvImages;
	Bitmap mBmpToWarn;


	//File mFileSaved;

	public static final int REQUEST_CODE_pickPhoto = 1;


	public static final int REQUEST_PICTURE_FROM_GALLERY = 23;
	public static final int REQUEST_PICTURE_FROM_CAMERA = 24;
	private static final String TAG = "ImageInputHelper";
	/**
	 * Id to identity SEND_SMS permission request.
	 */
	public static final int REQUEST_PERMISSIONS = 0;


	private File tempFileFromSource = null;
	private Uri tempUriFromSource = null;

	private static final String[] PERMISSIONS_TO_REQUEST =
			new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE};


	FloatingActionMenu fMenu;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mImageToWarn = findViewById(R.id.imageToWarn);
		mTvImages = findViewById(R.id.tvImages);
		int resource = R.drawable.movbg;
		Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), resource);
		mImageToWarn.setImageBitmap(bmp2);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {


			}
		});


		Window win = getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

			win.setStatusBarColor(Color.argb(0, 0, 0, 0));

		}
		FloatingActionMenu.Builder buildMenu = new FloatingActionMenu.Builder(this);


		int drw[] =
				new int[]{R.drawable.ic_create_white_24dp, R.drawable.ic_rotate_right_black_24dp,
						R.drawable.ic_share_black_24dp, R.drawable.ic_undo_black_24dp,
						R.drawable.ic_delete_black_24dp};
		ImageView[] img = new ImageView[drw.length];
		View.OnClickListener iconClick = new View.OnClickListener() {
			@Override public void onClick(View view) {

				switch ((int) view.getTag()) {
					case R.drawable.ic_create_white_24dp:
						if (mBmpToWarn == null) {
							Toast.makeText(MainActivity.this, R.string.empty_image_message,
									Toast.LENGTH_LONG).show();

						} else {
							displayDialogChooseColor();
						}

						break;
					case R.drawable.ic_rotate_right_black_24dp:

						if (mBmpToWarn == null) {
							Toast.makeText(MainActivity.this, R.string.empty_image_message,
									Toast.LENGTH_LONG).show();

						} else {
							mBmpToWarn = rotateBitmap(mBmpToWarn, 90);


							mImageToWarn.setImageBitmap(mBmpToWarn);
						}
						mFileContentUriSaved = null;

						break;
					case R.drawable.ic_rotate_left_black_24dp:

						if (mBmpToWarn == null) {
							Toast.makeText(MainActivity.this, R.string.empty_image_message,
									Toast.LENGTH_LONG).show();

						} else {
							mBmpToWarn = rotateBitmap(mBmpToWarn, -90);


							mImageToWarn.setImageBitmap(mBmpToWarn);
						}
						mFileContentUriSaved = null;

						break;
					case R.drawable.ic_share_black_24dp:
						actionShare();

						break;
					case R.drawable.ic_undo_black_24dp:
						if (mBmpToWarn == null) {
							Toast.makeText(MainActivity.this, R.string.empty_image_message,
									Toast.LENGTH_LONG).show();

						} else {
							mBmpToWarn = rotateBitmap(mBmpToWarn, -90);


							mImageToWarn.setImageBitmap(mBmpToWarn);
						}
						mFileContentUriSaved = null;
						/*if (mSelectedImage != null) {
							parseSelected(mSelectedImage);
							mFileContentUriSaved = null;
						}*/
						break;
					case R.drawable.ic_delete_black_24dp:

						mImageToWarn.setImageBitmap(null);
						mTvImages.setText(R.string.instruction_init);
						mFileContentUriSaved = null;
						mBmpToWarn = null;
						mSelectedImage = null;

						break;

				}
				fMenu.close(true);


			}
		};

		SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
		SubActionButton b;
		for (int i = 0; i < img.length; i++) {
			img[i] = new ImageView(this);
			img[i].setImageResource(drw[i]);

			img[i].setColorFilter(getResources().getColor(R.color.colorAccent));
			img[i].setOnClickListener(iconClick);

			b = itemBuilder.setContentView(img[i]).build();
			b.setTag(drw[i]);
			b.setOnClickListener(iconClick);

			buildMenu.addSubActionView(b);

		}
		int raddius = (int) getResources().getDimension(R.dimen.fab_radius);


		fMenu = buildMenu.attachTo(fab).setRadius(raddius).build();


		checkPermissions(this);
	}

	public Bitmap rotateBitmap(Bitmap original, float degrees) {
		int width = original.getWidth();
		int height = original.getHeight();

		Matrix matrix = new Matrix();
		matrix.preRotate(degrees);

		return Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
	}

	private void displayDialogChooseColor() {

		AlertDialog.Builder build =
				new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
		build.setTitle("Choose warning (1080x1920)").setMessage("")
				.setPositiveButton("BLACK", new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {


						displayDialogPosition(true);
					}
				}).setNegativeButton("WHITE", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {


				displayDialogPosition(false);
			}
		}).show();
	}

	private void displayDialogPosition(final boolean isBlack) {

		AlertDialog.Builder build =
				new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
		build.setTitle("Choose Placement Type").setMessage("")
				.setPositiveButton("EXTEND", new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						//mImageToWarn = null;
						mBmpToWarn = overTop(mBmpToWarn, isBlack);
						mImageToWarn.setImageBitmap(mBmpToWarn);
					}
				}).setNegativeButton("OVERLAY", new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {

				//mImageToWarn = null;
				mBmpToWarn = overlay(mBmpToWarn, isBlack);
				mImageToWarn.setImageBitmap(mBmpToWarn);
			}
		}).show();
	}

	private Bitmap overlay(Bitmap bmp1, boolean isBlack) {
		Toast.makeText(this, "overlay", Toast.LENGTH_SHORT).show();

		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig
				());
		int width = bmp1.getWidth();
		int height = bmp1.getHeight();
		int resource;

		double widthByHeight = width / height;
		double heightByWidth = height / width;

		if (width > height && widthByHeight <= 1.5) {

			if (isBlack) {
				resource = R.drawable.wide_warning_black_4x3;
			} else {
				resource = R.drawable.wide_warning_white_4x3;
			}
		}
		else if (width > height)
		{
			if (isBlack) {
				resource = R.drawable.wide_warning_black;
			} else {
				resource = R.drawable.wide_warning_white;
			}

		} else if ( height > width && heightByWidth <= 1.5) {
			if (isBlack) {
				resource = R.drawable.tall_warning_black_4x3;
			} else {
				resource = R.drawable.tall_warning_white_4x3;
			}
		}
		else
		{
			if (isBlack) {
				resource = R.drawable.tall_warning_black;
			} else {
				resource = R.drawable.tall_warning_white;
			}

		}
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);



		Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), resource);

		bmp2 = getResizedBitmap(bmp2, width, (int) (0.2 * height), true);
		canvas.drawBitmap(bmp2, new Matrix(), null);
		return bmOverlay;
	}


	private Bitmap overTop(Bitmap bmp1, boolean isBlack) {

		Toast.makeText(this, "overTop", Toast.LENGTH_SHORT).show();


		int width = bmp1.getWidth();
		int height = bmp1.getHeight();
		int resource;

		double widthByHeight = width / height;
		double heightByWidth = height / width;


		if (width > height  && widthByHeight <= 1.5) {
			if (isBlack) {
				resource = R.drawable.wide_warning_black_4x3_extend;
			} else {
				resource = R.drawable.wide_warning_white_4x3_extend;
			}

		}
		else if (width > height)
		{
			if (isBlack) {
				resource = R.drawable.wide_warning_black_extend;
			} else {
				resource = R.drawable.wide_warning_white_extend;
			}
		}
		else if (height > width  && heightByWidth <= 1.5) {
			if (isBlack) {
				resource = R.drawable.tall_warning_black_4x3_extend;
			} else {
				resource = R.drawable.tall_warning_white_4x3_extend;
			}
		}
		else {
			if (isBlack) {
				resource = R.drawable.tall_warning_black_extend;
			} else {
				resource = R.drawable.tall_warning_white_extend;
			}

		}
		Bitmap bmp2 = BitmapFactory.decodeResource(getResources(), resource);


		Bitmap bmOverlay =
				Bitmap.createBitmap(bmp1.getWidth(), (int) (bmp1.getHeight() * 1.25), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		Matrix m = new Matrix();


		bmp2 = getResizedTranslatedBitmap(bmp2, width, (int) (0.25 * height), false);
		m.postTranslate(0, bmp2.getHeight());
		canvas.drawBitmap(bmp1, m, null);

		canvas.drawBitmap(bmp2, new Matrix(), null);
		return bmOverlay;
	}


	public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight,
										  boolean isNecessaryToKeepOrig) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap =
				Bitmap.createBitmap(bm, 0, 0, width, height, matrix, isNecessaryToKeepOrig);
		if (!isNecessaryToKeepOrig) {
			bm.recycle();
		}
		return resizedBitmap;
	}

	private static Bitmap getResizedTranslatedBitmap(Bitmap bm, int newWidth, int newHeight,
													 boolean isNecessaryToKeepOrig) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();

		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		if (!isNecessaryToKeepOrig) {
			bm.recycle();
		}
		return resizedBitmap;
	}


	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		if (requestCode == REQUEST_CODE_pickPhoto) {
			if (resultCode == RESULT_OK) {

				mSelectedImage = data.getData();

				parseSelected(mSelectedImage, true);


			}
		} else {

			if ((requestCode == REQUEST_PICTURE_FROM_GALLERY) &&
					(resultCode == Activity.RESULT_OK)) {


				mSelectedImage = data.getData();


				if (mSelectedImage == null) {
				}
				else {

					parseSelected(mSelectedImage, false);
				}


			} else if ((requestCode == REQUEST_PICTURE_FROM_CAMERA) &&
					(resultCode == Activity.RESULT_OK)) {

				try {

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = calculateInSampleSize(options, 1200,1000);
					options.inJustDecodeBounds = false;

					if (mSelectedImage == null)
					Log.d("TAG", "mSelectedImage is null");
					InputStream is = getContentResolver().openInputStream(mSelectedImage);
					Rect outPad = new Rect();
					mBmpToWarn = BitmapFactory.decodeStream(is, outPad, options);
					is.close();

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					mBmpToWarn.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					byte[] bitmapdata = bos.toByteArray();
					mBmpToWarn = BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));
					bitmapdata = null;

					mImageToWarn.setImageBitmap(mBmpToWarn);


				} catch (Exception e) {
					e.printStackTrace();
				}


				Log.d(TAG, "Image selected from camera");


			}

		}
	}

	public static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight
					&& (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	private void parseSelected(Uri selectedImage, boolean fromCamera) {


		mFilePath = getPath(selectedImage);
		Log.d(TAG,  "File path" + mFilePath);
		String file_extn = mFilePath.substring(mFilePath.lastIndexOf(".") + 1);


		try {
			if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") ||
					file_extn.equals("gif") || file_extn.equals("png")) {

				String imgSelected = null;


				try {
					Uri uri = Uri.parse(mFilePath);

					if (uri != null) {
						imgSelected = uri.getLastPathSegment();
						mTvImages.setText(imgSelected);


						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = calculateInSampleSize(options, 1200,1000);
						options.inJustDecodeBounds = false;
						mBmpToWarn = BitmapFactory.decodeFile(mFilePath, options);

						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						mBmpToWarn.compress(Bitmap.CompressFormat.JPEG, 100, bos);
						byte[] bitmapdata = bos.toByteArray();
						mBmpToWarn = BitmapFactory.decodeStream(new ByteArrayInputStream(bos.toByteArray()));
						bitmapdata = null;
						if (fromCamera)
						{
							if (mBmpToWarn.getHeight() > mBmpToWarn.getWidth()) {
								mBmpToWarn = rotateBitmap(mBmpToWarn, 90);
							}
						}

						mImageToWarn.setImageBitmap(mBmpToWarn);

					}
				} catch (Exception e) {

				}


			} else {
				Toast.makeText(this, "Invalid format", Toast.LENGTH_SHORT).show();

				//NOT IN REQUIRED FORMAT
			}
		} catch (Exception e) {
			Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public String getPath(Uri uri) {
		String[] projection = {MediaStore.MediaColumns.DATA};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
		cursor.moveToFirst();
		String imagePath = cursor.getString(column_index);
		cursor.close();

		return imagePath;
	}

	public void onPickImage() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");

		try {
			startActivityForResult(photoPickerIntent, REQUEST_CODE_pickPhoto);

		} catch (Exception e) {

			Toast.makeText(this,
					"Error " + new Exception().getStackTrace()[0] + " , contact developer",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	final SimpleDateFormat timeFormat = new SimpleDateFormat("HH_mm_ss", Locale.US);

	final SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yy", Locale.US);


	@Override public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_pick_galery) {

			onPickImage();
			return true;
		}

		if (id == R.id.action_save) {

			onSaveImageStore();


			return true;
		}

		if (id == R.id.action_view_gallery) {

			selectImageFromGallery();

			return true;
		}


		if (id == R.id.action_take_photo) {

			takePhotoWithCamera();


			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	void onSaveImageStore() {
		if (mBmpToWarn == null || mSelectedImage == null) {
			Toast.makeText(this, R.string.empty_image_message, Toast.LENGTH_LONG).show();
			return;
		}
		Date date = new Date();

		String fname =
				"Img_warned_" + dateFormat.format(date) + "_" + timeFormat.format(date) + ".jpg";
		mFileContentUriSaved =
				MediaStore.Images.Media.insertImage(getContentResolver(), mBmpToWarn, fname, fname);
		if (mFileContentUriSaved == null) {
			Toast.makeText(this, "ERROR SAVING IMAGE", Toast.LENGTH_LONG).show();
		} else {


			new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
					.setTitle("").setMessage("File: " + fname + " saved!")
					.setPositiveButton("Close", null).setNeutralButton("Share",

					new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialogInterface, int i) {
							actionShare();
						}
					}).setNegativeButton("Open",

					new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialogInterface, int i) {
							actionOpenGalery(mFileContentUriSaved);
						}
					}).show();
		}
	}

	private void onSave() {
		if (mBmpToWarn == null || mSelectedImage == null) {
			Toast.makeText(this, R.string.empty_image_message, Toast.LENGTH_LONG).show();
			return;
		}

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/Warn");
		myDir.mkdirs();


		Date date = new Date();

		String fname = dateFormat.format(date) + "_" + timeFormat.format(date) + ".jpg";


		File file = new File(myDir, fname);
		Log.e("okAG", "okAG " + " " + new Exception().getStackTrace()[0].toString());//TODO


		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			mBmpToWarn.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {


			Toast.makeText(this,
					"Error " + e.getMessage() + " " + new Exception().getStackTrace()[0] +
							" , contact developer", Toast.LENGTH_LONG).show();

			e.printStackTrace();
		}
		//mFileSaved = file;

		new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
				.setTitle("").setMessage("saving at: " + myDir + "/" + fname)
				.setPositiveButton("Close", null).setNeutralButton("Share",

				new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialogInterface, int i) {
						actionShare();
					}
				}).show();
	}

	private void actionShare() {

		if (mFileContentUriSaved == null) {
			if (mBmpToWarn == null) {
				Toast.makeText(this, R.string.empty_image_message, Toast.LENGTH_LONG).show();
			} else {
				onSaveImageStore();
			}

		} else {

			try {

				final Intent shareIntent = new Intent(Intent.ACTION_SEND);
				shareIntent.setType("image/jpg");
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mFileContentUriSaved));
				startActivity(Intent.createChooser(shareIntent, "Share image using"));

			} catch (Exception e) {
				e.printStackTrace();

				actionOpenGalery(mFileContentUriSaved);


			}
		}

	}

	void actionOpenGalery(String path) {


		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setType("image/*");
		if (path != null) {


			intent.setData(Uri.parse(path));
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			if (path != null) {
				grantUriPermissions(Intent.FLAG_GRANT_READ_URI_PERMISSION, intent, Uri.parse
						(path));

			}
			startActivity(intent);


		} catch (Exception e) {


			Toast.makeText(this,
					"Error " + new Exception().getStackTrace()[0] + " , contact developer",
					Toast.LENGTH_LONG).show();

		}
	}


	public static boolean checkPermissions(Activity act) {
		return checkPermissions(act, true);
	}

	public static boolean checkPermissions(Activity act, boolean request) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		boolean isPermissionGranted = true;
		for (String obj : PERMISSIONS_TO_REQUEST) {
			if (!(act.checkSelfPermission(obj) == PackageManager.PERMISSION_GRANTED)) {
				isPermissionGranted = false;
				break;
			}

		}
		if (isPermissionGranted) {
			return true;
		} else {
			if (request) {
				act.requestPermissions(PERMISSIONS_TO_REQUEST, REQUEST_PERMISSIONS);
			}

		}
		return false;
	}

	/**
	 * Callback received when a permissions request has been completed.
	 */
	@Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
													 int[] grantResults) {
		if (requestCode == REQUEST_PERMISSIONS) {

			boolean isPermissionGranted = true;
			for (int result : grantResults) {
				if (!(result == PackageManager.PERMISSION_GRANTED)) {
					isPermissionGranted = false;
					break;
				}

			}
			if (isPermissionGranted) {

			} else {
				Toast.makeText(this, "You can't continue without granting permissions",
						Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	/**
	 * Starts an intent for selecting image from gallery. The result is returned to the
	 * onImageSelectedFromGallery() method of the ImageSelectionListener interface.
	 */
	public void selectImageFromGallery() {

		if (tempFileFromSource == null) {
			try {
				tempFileFromSource = File.createTempFile("choose", "png", getExternalCacheDir());
				tempUriFromSource = Uri.fromFile(tempFileFromSource);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		intent.putExtra("return-data", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, true);
		startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), REQUEST_PICTURE_FROM_GALLERY);


	}

	/**
	 * Starts an intent for taking photo with camera. The result is returned to the
	 * onImageTakenFromCamera() method of the ImageSelectionListener interface.
	 */
	public void takePhotoWithCamera() {
		dispatchTakePictureIntent();


	}


	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (Exception ex) {
				Log.e("okAG", "okAG Error creating temp File" + ex.getMessage() + " " +
						new Exception().getStackTrace()[0]
								.toString());//TODO okAG edit clear this logs


			}
			// Continue only if the File was successfully created
			if (photoFile != null && photoFile.exists()) {


				try {
					mSelectedImage = FileProvider
							.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImage);
					takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
							Intent.FLAG_GRANT_READ_URI_PERMISSION);

					grantUriPermissions(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
									Intent.FLAG_GRANT_READ_URI_PERMISSION, takePictureIntent,
							mSelectedImage);
					startActivityForResult(takePictureIntent, REQUEST_PICTURE_FROM_CAMERA);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this,
							"Error " + e.getMessage() + " " + new Exception().getStackTrace()[0] +
									" , contact developer", Toast.LENGTH_LONG).show();
				}


			}


		}
	}


	public void grantUriPermissions(int permissions, Intent intent, Uri uri) {
		List<ResolveInfo> resInfoList =
				getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			grantUriPermission(packageName, uri, permissions);
		}


	}

	String mCapturedPhotoPath;

	private File createImageFile() throws IOException {
		Date date = new Date();

		String fname = "Img_warned_" + dateFormat.format(date) + "_" + timeFormat.format(date);
		// Create an image file name
		File storageDir =getExternalCacheDir();

		File image = File.createTempFile(fname,  /* prefix */
				".jpg",         /* suffix */
				storageDir
				/* directory */);


		// Save a file: path for use with ACTION_VIEW intents
		mCapturedPhotoPath = image.getAbsolutePath();
		return image;
	}


}
