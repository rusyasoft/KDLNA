package keti.soc.cnd.kdlna;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;

import mediaserver.ContentNode;
import mediaserver.ContentTree;
import mediaserver.MediaServer;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;

//// rustamchange// following lines related to adding fixed device changes
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.model.types.UDN;

import keti.soc.cnd.kdlna.client.ui.DeviceArrayAdapterWithImage;
import keti.soc.cnd.kdlna.test.*;
///////////////////////////////////////////////////////////////////////////

/* rustamchange/// turned off, because not figured out 
 import com.wireme.R;
 import com.wireme.activity.DeviceItem;
 import com.wireme.activity.MainActivity;
 import com.wireme.mediaserver.MediaServer;
 */

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;

import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.Service;

import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback;
//import org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.Res;
import org.teleal.cling.support.model.WriteStatus;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.ImageItem;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.VideoItem;
import org.teleal.common.util.MimeType;

//import com.wireme.activity.DeviceItem;
//import com.wireme.activity.MainActivity.DeviceListRegistryListener;

/*
 import com.wireme.mediaserver.ContentNode;
 import com.wireme.mediaserver.ContentTree;
 import com.wireme.R;
 import com.wireme.activity.MainActivity;
 import com.wireme.mediaserver.MediaServer;
 */

public class MainActivity extends Activity {

	// private ArrayAdapter<DeviceDisplay> listAdapter;
	private DeviceArrayAdapterWithImage ServerListAdapter;

	public static ArrayAdapter<DeviceItem> deviceListAdapter;
	public static ArrayAdapter<ContentItem> contentListAdapter;

	public ListView ServerListView;

	public static Activity MainActivityThis = null;
	public static AndroidUpnpService upnpService; // declaration of
													// AndroidUpnpService object

	// private RegistryListener registryListener = new BrowseRegistryListener();
	private BrowseRegistryListener registryListener = new BrowseRegistryListener();

	private MediaServer mediaServer;
	private static boolean serverPrepared = false;
	private final static String LOGTAG = "KDLNA";

	static public Device SelectedDevice = null;
	static public Service SelectedService = null;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			upnpService = (AndroidUpnpService) service;

			// // creation of UPnP Server
			if (mediaServer == null) {
				try {
					mediaServer = new MediaServer(getLocalIpAddress());
					upnpService.getRegistry()
							.addDevice(mediaServer.getDevice());
					prepareMediaServer();
				} catch (Exception ex) {
					// TODO: handle exception
					Log.v("MainActivity", "Creating demo device failed");
					Toast.makeText(MainActivity.this,
							"Creating of device failed, check the log!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			// //////////////////////////////

			// Refresh the list with all known devices
			ServerListAdapter.clear();
			for (Device device : upnpService.getRegistry().getDevices()) {
				// rustamchange// didnt work, since RegistryListener doesn't
				// have deviceAdded()
				// method//registryListener.deviceAdded(device);
				// rustamchange// trying other existing methods
				registryListener.deviceAdded(upnpService.getRegistry(), device);
				// ////////////////////////
			}

			/*
			 * ////rustamchange/// trying to add new fixed device try {
			 * RemoteDevice discoveredDevice = new
			 * RemoteDevice(SampleData.createRemoteDeviceIdentity());
			 * registryListener.deviceAdded(upnpService.getRegistry(),
			 * discoveredDevice); } catch (ValidationException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 * ///////////////////////////////////////////////////
			 */

			// Getting ready for future device advertisements
			upnpService.getRegistry().addListener(registryListener);

			// Search asynchronously for all devices
			upnpService.getControlPoint().search();

			// /////////////////////////////////////////////////////////////////
			// //rustamchange//// poligon area for trying different experiments

			// Notification of aliveness//
			// upnpService.get().getProtocolFactory().createSendingNotificationAlive(mediaServer.getDevice()).run();
			/*
			 * Thread thread = new Thread() {
			 * 
			 * @Override public void run() { try { while(true) { sleep(2000);
			 * //Toast.makeText(getApplicationContext(), "Running Thread...",
			 * Toast.LENGTH_LONG).show(); Log.v("ServiceConnection",
			 * "Running Thread...");
			 * upnpService.get().getProtocolFactory().createSendingNotificationAlive
			 * (mediaServer.getDevice()).run(); } } catch (InterruptedException
			 * e) { Log.v("ServiceConnection", e.toString()); } } };
			 * 
			 * thread.start();
			 */
			// ///////////////////////////////////////////////////////////////////

		}

		public void onServiceDisconnected(ComponentName className) {
			upnpService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ServerListView = (ListView) findViewById(R.id.listView1);
		ServerListAdapter = new DeviceArrayAdapterWithImage(
				ServerListView.getContext(), R.id.listView1);

		// DeviceArrayAdapterWithImage adapterWithImage = new
		// DeviceArrayAdapterWithImage(this, SERVERS);
		ServerListView.setAdapter(ServerListAdapter);
		ServerListView.setOnItemClickListener(deviceItemClickListener);

		// deviceListAdapter = new
		// ArrayAdapter<DeviceItem>(this,android.R.layout.simple_list_item_1);
		// deviceListRegistryListener = new DeviceListRegistryListener(); ///
		// registryListener is declared and created as a global
		// BrowseRegistryListener class which is derrived from
		// DefaultRegistryListener
		// deviceListView.setAdapter(deviceListAdapter);
		// deviceListView.setOnItemClickListener(deviceItemClickListener);

		// deviceListAdapter = new
		// ArrayAdapter<DeviceItem>(this,android.R.layout.simple_list_item_1);
		// deviceListRegistryListener = new DeviceListRegistryListener();
		// deviceListView.setAdapter(deviceListAdapter);
		// deviceListView.setOnItemClickListener(deviceItemClickListener);

		getApplicationContext().bindService(
				new Intent(this, AndroidUpnpServiceImpl.class),
				serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (upnpService != null) {
			upnpService.getRegistry().removeListener(registryListener);
		}
		getApplicationContext().unbindService(serviceConnection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.search_lan).setIcon(
				android.R.drawable.ic_menu_search);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0 && upnpService != null) {
			upnpService.getRegistry().removeAllRemoteDevices();
			upnpService.getControlPoint().search();
		}
		return false;
	}

	// //////// DeviceDisplay ///////////////
	/*
	 * class DeviceDisplay { Device device;
	 * 
	 * public DeviceDisplay(Device device) { this.device = device; }
	 * 
	 * public Device getDevice() { return device; }
	 * 
	 * @Override public boolean equals(Object o) { if (this == o) return true;
	 * if (o == null || getClass() != o.getClass()) return false; DeviceDisplay
	 * that = (DeviceDisplay) o; return device.equals(that.device); }
	 * 
	 * @Override public int hashCode() { return device.hashCode(); }
	 * 
	 * @Override public String toString() { // Display a little star while the
	 * device is being loaded //rustamchange//original code for toString//return
	 * device.isFullyHydrated() ? device.getDisplayString() :
	 * device.getDisplayString() + " *";
	 * 
	 * //rustamchange/// code from wireme String display;
	 * 
	 * if (device.getDetails().getFriendlyName() != null) display =
	 * device.getDetails().getFriendlyName(); else display =
	 * device.getDisplayString();
	 * 
	 * // Display a little star while the device is being loaded (see //
	 * performance optimization earlier) return device.isFullyHydrated() ?
	 * display : display + " *"; } }
	 */
	// //////// END //// DeviceDisplay /////////////

	// ////////////////////////////////////////////////////////////
	// ///////////////// BrowseRegistryListener ///////////////////
	// ////////////////////////////////////////////////////////////
	class BrowseRegistryListener extends DefaultRegistryListener {

		@Override
		public void remoteDeviceDiscoveryStarted(Registry registry,
				RemoteDevice device) {
			deviceAdded(device);
		}

		@Override
		public void remoteDeviceDiscoveryFailed(Registry registry,
				final RemoteDevice device, final Exception ex) {
			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(
							MainActivity.this,
							"Discovery failed of '"
									+ device.getDisplayString()
									+ "': "
									+ (ex != null ? ex.toString()
											: "Couldn't retrieve device/service descriptors"),
							Toast.LENGTH_LONG).show();
				}
			});
			deviceRemoved(device);
		}

		@Override
		public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
			deviceAdded(device);

			// //rustamchange/// trying to add new fixed device
			try {
				RemoteDevice discoveredDevice = new StaticCndTestDevice(); //new StaticTestRemoteDevice();
				
				/*RemoteDevice discoveredDevice = new RemoteDevice(
						SampleData.createRemoteDeviceIdentity(),
						new UDADeviceType("MyDevice"),
						new DeviceDetails("JustATest"),
						new RemoteService(
								new UDAServiceType("ContentDirectory"),
								new UDAServiceId("ContentDirectory"),
								URI.create("/scpd.xml"),
								URI.create("/control"),
								URI.create("/events"),
								new Action[] { new Action(
										"GetNegativeValue",
										new ActionArgument[] { new ActionArgument(
												"Result", "NegativeValue",
												ActionArgument.Direction.OUT) }) },
								new StateVariable[] { new StateVariable(
										"NegativeValue",
										new StateVariableTypeDetails(
												Datatype.Builtin.UI4
														.getDatatype()),
										new StateVariableEventDetails(false)) }));
				*/
				
				deviceAdded(discoveredDevice);
				// registryListener.deviceAdded(upnpService.getRegistry(),
				// discoveredDevice);
			} catch (ValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// /////////////////////////////////////////////////

			// / Saving device object into file
			/*
			 * try {
			 * 
			 * //Device dev1= new Device(null, DeviceType, null, null, null,
			 * null, null ); FileOutputStream fout =
			 * openFileOutput("ru_address.ser", Context.MODE_PRIVATE);
			 * ObjectOutputStream oos = new ObjectOutputStream(fout);
			 * oos.writeObject(device); oos.close(); fout.close();
			 * 
			 * //FileInputStream fin = openFileInput("ru_address.ser");
			 * //ObjectInputStream ois = new ObjectInputStream(fin); //Object
			 * object; //try { // object = ois.readObject(); //} catch
			 * (ClassNotFoundException e) { // TODO Auto-generated catch block
			 * // e.printStackTrace(); //} //ois.close(); //fin.close();
			 * 
			 * } catch (FileNotFoundException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */

			// ////////////////////////////////////
		}

		@Override
		public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
			deviceRemoved(device);
		}

		@Override
		public void localDeviceAdded(Registry registry, LocalDevice device) {
			deviceAdded(device);
		}

		@Override
		public void localDeviceRemoved(Registry registry, LocalDevice device) {
			deviceRemoved(device);
		}

		public void deviceAdded(final Device device) {
			runOnUiThread(new Runnable() {
				public void run() {
					DeviceDisplay d = new DeviceDisplay(device);
					int position = ServerListAdapter.getPosition(d);
					if (position >= 0) {
						// Device already in the list, re-set new value at same
						// position
						ServerListAdapter.insert(d, position);
						ServerListAdapter.remove(d);
					} else {
						ServerListAdapter.add(d);
					}
				}
			});
		}

		public void deviceRemoved(final Device device) {
			runOnUiThread(new Runnable() {
				public void run() {
					ServerListAdapter.remove(new DeviceDisplay(device));
				}
			});
		}
	}

	// ////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////

	private void prepareMediaServer() {

		if (serverPrepared)
			return;

		ContentNode rootNode = ContentTree.getRootNode();
		// Video Container
		Container videoContainer = new Container();
		videoContainer.setClazz(new DIDLObject.Class("object.container"));
		videoContainer.setId(ContentTree.VIDEO_ID);
		videoContainer.setParentID(ContentTree.ROOT_ID);
		videoContainer.setTitle("Videos");
		videoContainer.setRestricted(true);
		videoContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
		videoContainer.setChildCount(0);

		rootNode.getContainer().addContainer(videoContainer);
		rootNode.getContainer().setChildCount(
				rootNode.getContainer().getChildCount() + 1);
		ContentTree.addNode(ContentTree.VIDEO_ID, new ContentNode(
				ContentTree.VIDEO_ID, videoContainer));

		Cursor cursor;
		String[] videoColumns = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.ARTIST,
				MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.SIZE,
				MediaStore.Video.Media.DURATION,
				MediaStore.Video.Media.RESOLUTION };
		cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				videoColumns, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String id = ContentTree.VIDEO_PREFIX
						+ cursor.getInt(cursor
								.getColumnIndex(MediaStore.Video.Media._ID));
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
				String creator = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
				String filePath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				String mimeType = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
				long duration = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
				String resolution = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
				Res res = new Res(new MimeType(mimeType.substring(0,
						mimeType.indexOf('/')), mimeType.substring(mimeType
						.indexOf('/') + 1)), size, "http://"
						+ mediaServer.getAddress() + "/" + id);
				res.setDuration(duration / (1000 * 60 * 60) + ":"
						+ (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
						+ (duration % (1000 * 60)) / 1000);
				res.setResolution(resolution);

				VideoItem videoItem = new VideoItem(id, ContentTree.VIDEO_ID,
						title, creator, res);
				videoContainer.addItem(videoItem);
				videoContainer
						.setChildCount(videoContainer.getChildCount() + 1);
				ContentTree.addNode(id,
						new ContentNode(id, videoItem, filePath));

				Log.v(LOGTAG, "added video item " + title + "from " + filePath);
			} while (cursor.moveToNext());
		}

		// Audio Container
		Container audioContainer = new Container(ContentTree.AUDIO_ID,
				ContentTree.ROOT_ID, "Audios", "GNaP MediaServer",
				new DIDLObject.Class("object.container"), 0);
		audioContainer.setRestricted(true);
		audioContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
		rootNode.getContainer().addContainer(audioContainer);
		rootNode.getContainer().setChildCount(
				rootNode.getContainer().getChildCount() + 1);
		ContentTree.addNode(ContentTree.AUDIO_ID, new ContentNode(
				ContentTree.AUDIO_ID, audioContainer));

		String[] audioColumns = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE,
				MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM };
		cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				audioColumns, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String id = ContentTree.AUDIO_PREFIX
						+ cursor.getInt(cursor
								.getColumnIndex(MediaStore.Audio.Media._ID));
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				String creator = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				String filePath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				String mimeType = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
				long duration = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
				String album = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
				Res res = new Res(new MimeType(mimeType.substring(0,
						mimeType.indexOf('/')), mimeType.substring(mimeType
						.indexOf('/') + 1)), size, "http://"
						+ mediaServer.getAddress() + "/" + id);
				res.setDuration(duration / (1000 * 60 * 60) + ":"
						+ (duration % (1000 * 60 * 60)) / (1000 * 60) + ":"
						+ (duration % (1000 * 60)) / 1000);

				// Music Track must have `artist' with role field, or
				// DIDLParser().generate(didl) will throw nullpointException
				MusicTrack musicTrack = new MusicTrack(id,
						ContentTree.AUDIO_ID, title, creator, album,
						new PersonWithRole(creator, "Performer"), res);
				audioContainer.addItem(musicTrack);
				audioContainer
						.setChildCount(audioContainer.getChildCount() + 1);
				ContentTree.addNode(id, new ContentNode(id, musicTrack,
						filePath));

				Log.v(LOGTAG, "added audio item " + title + "from " + filePath);
			} while (cursor.moveToNext());
		}

		// Image Container
		Container imageContainer = new Container(ContentTree.IMAGE_ID,
				ContentTree.ROOT_ID, "Images", "GNaP MediaServer",
				new DIDLObject.Class("object.container"), 0);
		imageContainer.setRestricted(true);
		imageContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
		rootNode.getContainer().addContainer(imageContainer);
		rootNode.getContainer().setChildCount(
				rootNode.getContainer().getChildCount() + 1);
		ContentTree.addNode(ContentTree.IMAGE_ID, new ContentNode(
				ContentTree.IMAGE_ID, imageContainer));

		String[] imageColumns = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE };
		cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				imageColumns, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				String id = ContentTree.IMAGE_PREFIX
						+ cursor.getInt(cursor
								.getColumnIndex(MediaStore.Images.Media._ID));
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
				String creator = "unkown";
				String filePath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
				String mimeType = cursor
						.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
				long size = cursor.getLong(cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));

				Res res = new Res(new MimeType(mimeType.substring(0,
						mimeType.indexOf('/')), mimeType.substring(mimeType
						.indexOf('/') + 1)), size, "http://"
						+ mediaServer.getAddress() + "/" + id);

				ImageItem imageItem = new ImageItem(id, ContentTree.IMAGE_ID,
						title, creator, res);
				imageContainer.addItem(imageItem);
				imageContainer
						.setChildCount(imageContainer.getChildCount() + 1);
				ContentTree.addNode(id,
						new ContentNode(id, imageItem, filePath));

				Log.v(LOGTAG, "added image item " + title + "from " + filePath);
			} while (cursor.moveToNext());
		}

		serverPrepared = true;
	}

	// FIXME: now only can get wifi address
	private InetAddress getLocalIpAddress() throws UnknownHostException {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return InetAddress.getByName(String.format("%d.%d.%d.%d",
				(ipAddress & 0xff), (ipAddress >> 8 & 0xff),
				(ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff)));
	}

	// //////////////////////////////////////// related to click ////////////
	OnItemClickListener deviceItemClickListener = new OnItemClickListener() {

		protected Container createRootContainer(Service service) {
			Container rootContainer = new Container();
			rootContainer.setId("0");
			rootContainer.setTitle("Content Directory on "
					+ service.getDevice().getDisplayString());
			return rootContainer;
		}

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			int howManyDevicesFound = ServerListAdapter.getCount();
			if (position < howManyDevicesFound) {
				SelectedDevice = ServerListAdapter.getItem(position).getDevice();
				SelectedService = SelectedDevice.findService(new UDAServiceType("ContentDirectory"));

				MainActivityThis = MainActivity.this;

				/*
				 * upnpService.getControlPoint().execute( new
				 * ContentBrowseActionCallback(MainActivity.this,
				 * SelectedService, createRootContainer(SelectedService),
				 * contentListAdapter));
				 */

				Intent myIntent = new Intent();
				myIntent.setClassName("keti.soc.cnd.kdlna",
						"keti.soc.cnd.kdlna.ContentActivity");
				startActivity(myIntent);

			} else
				System.out.println("Position:" + position
						+ " was requested out of " + howManyDevicesFound
						+ " elements");

		}
	};
}
