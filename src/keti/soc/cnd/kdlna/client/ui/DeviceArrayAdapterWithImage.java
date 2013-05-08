package keti.soc.cnd.kdlna.client.ui;



import java.util.ArrayList;
import java.util.List;

import keti.soc.cnd.kdlna.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import keti.soc.cnd.kdlna.DeviceDisplay;

public class DeviceArrayAdapterWithImage extends ArrayAdapter<DeviceDisplay>
{
	private final Context  context;
    //private DeviceDisplay[] values = new DeviceDisplay[20];
	private List<DeviceDisplay> values = new ArrayList<DeviceDisplay>();
	
    private int numberOfvalues = 0;

    public DeviceArrayAdapterWithImage(Context context,  int textViewResourceId) {
		super(context, R.layout.rowlayout);
		this.context = context;		
		// TODO Auto-generated constructor stub
	}
    /*
    public DeviceArrayAdapterWithImage(Context context, String[] values )
    {
    	super(context, R.layout.rowlayout);
        this.context = context;
        this.values = values;
    }
    */
    
    /*
    public DeviceArrayAdapterWithImage(Context context, String[] values)
    {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.values = values;
    }
    */
    
    @Override 
    public int getCount()
    {
    	return values!=null ? values.size() : 0;
    }
    
    
    @Override
    public void add(DeviceDisplay device)
    {   
    	values.add(device);
    }
    
    
    @Override
    public void insert(DeviceDisplay device, int insert_index)
    {
    	if (insert_index > 0)
    		values.add(insert_index-1, device);
    	else 
    		values.add(insert_index, device);
    }
    
    @Override
    public DeviceDisplay getItem(int posit)
    {
    	return values.get(posit);
    }
    
    /*
    @Override
    public void remove(DeviceDisplay device)
    {
    	values.remove(device);
    }
    */
    
    
    @Override
    public int getPosition(DeviceDisplay device)
    {
    	return values.indexOf(device);
    	/*
    	for (int i=0;i<numberOfvalues;i++)
    	{
    		if (device.toString() == values[i].toString())
    			return i;
    	}
    	*/
    	//return -1;
    }

    
    @Override
    public String toString() {    	
        return values.toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);        
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position).toString());
        // Change the icon for Windows and iPhone
        String s = values.get(position).toString();
        //imageView.setImageResource(R.drawable.icon_video_ov_pr);        
        switch(position)
        {
	        case 0:
	        case 1: imageView.setImageResource(R.drawable.android_icon); break;
	        case 2: imageView.setImageResource(R.drawable.mserver_icon); break;
	        case 3: imageView.setImageResource(R.drawable.laptop_icon); break;
	        case 4: imageView.setImageResource(R.drawable.phone_icon); break;
	        default: imageView.setImageResource(R.drawable.android_icon); break;        
        };

        return rowView;
    }
}
