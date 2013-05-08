package keti.soc.cnd.kdlna;

import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.support.contentdirectory.ui.ContentBrowseActionCallback;
import org.teleal.cling.support.model.container.Container;



import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ContentActivity extends Activity {

	public String strIP_ADDR = "192.168.0.55";
	
	public static Device SelectedDevice = null; 
	
	private ArrayAdapter<ContentItem> contentListAdapter;
	private ListView contentListView;
	
	
    public ContentActivity()
    {
    	//strIP_ADDR = IpSelectionActivity.SelectedIP; //ipaddr;
    	Log.v("ContentActivity", "Constructor>> Selected Server: "+strIP_ADDR);
    }

    public static class MediaListMessage
    {
    	public  int NumOfMediaFiles = 0;
    	public  String MediaFileList[];
    };    
    
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	// changing dialog icon
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.folders_and_media_contents);
		this.findViewById(android.R.id.content);
		// changing dialog icon
		////////////////////////////////////////////////////////////////////////////////////
    	contentListView = (ListView) findViewById(R.id.listView2);
    	
    	
    	contentListAdapter = new ArrayAdapter<ContentItem>(this,
    			android.R.layout.simple_list_item_1);
		contentListView.setAdapter(contentListAdapter);
		contentListView.setOnItemClickListener(contentItemClickListener);

    	SelectedDevice = MainActivity.SelectedDevice;
    	Service service = SelectedDevice.findService(new UDAServiceType("ContentDirectory"));
		
		MainActivity.upnpService.getControlPoint().execute(
				new ContentBrowseActionCallback(
						this, 
						MainActivity.SelectedService, 
						createRootContainer(MainActivity.SelectedService), 
						contentListAdapter
						)
				);
        
        
         
		//upnpService.getControlPoint().execute(
		//		new ContentBrowseActionCallback(MainActivity.this, SelectedService,
		//				createRootContainer(SelectedService), contentListAdapter));
		
		
          
		Log.v("RUSTAM>>>", ">>>> End");
    }
    
    protected Container createRootContainer(Service service) {
		Container rootContainer = new Container();
		rootContainer.setId("0");
		rootContainer.setTitle("Content Directory on "
				+ service.getDevice().getDisplayString());
		return rootContainer;
	}
    
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    OnItemClickListener contentItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			// TODO Auto-generated method stub
			ContentItem content = contentListAdapter.getItem(position);
			
			// checking the content for being multimedia {audio, video}
			if (content.isContainer()) {
				MainActivity.upnpService.getControlPoint().execute(
						new ContentBrowseActionCallback(ContentActivity.this,
								content.getService(), content.getContainer(),
								contentListAdapter));
			} else {
				
				System.out.println(" ------------- NOW READY TO RUN THE MEDIA -------------");				
				Intent intent = new Intent();
				intent.setClass(ContentActivity.this, GPlayer.class);
				String remoteFileURI =  content.getItem().getFirstResource().getValue();
				System.out.println(" ------------- Remote File URI =" + remoteFileURI);
				intent.putExtra("playURI", remoteFileURI);
				startActivity(intent);
				
			}
		}
	};


    
}
