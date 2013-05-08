

package keti.soc.cnd.kdlna;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

import keti.soc.cnd.kdlna.test.SampleData;
import keti.soc.cnd.kdlna.test.SampleDeviceRoot;

import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteDeviceIdentity;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.meta.StateVariableEventDetails;
import org.teleal.cling.model.meta.StateVariableTypeDetails;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.transport.impl.NetworkAddressFactoryImpl;
import org.teleal.common.util.URIUtil;

public class StaticCndTestDevice extends RemoteDevice {
	
	public static int maxAgeSeconds = 1800;
	
	InetAddress localaddress = null;
	
	
	public static RemoteDeviceIdentity remoteDeviceIdentity = new RemoteDeviceIdentity(
			new UDN("13131314-b0de-10e9-0000-0000171cd062"),
            maxAgeSeconds,
            URIUtil.createAbsoluteURL( MakeURL(), URI.create("/dev/13131314-b0de-10e9-0000-0000171cd062/desc.xml")),
            null,
            getLocalBaseAddress()
    );
	
	public static URL MakeURL() {
        try {
            return new URL("http://192.168.0.58:8192/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static InetAddress getLocalBaseAddress() {
        try {
            return InetAddress.getByName("192.168.0.60"); //getLocalHost();// 
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }
	
	public StaticCndTestDevice() throws ValidationException
	{
		super(
				remoteDeviceIdentity, //SampleData.createRemoteDeviceIdentity(),
				new UDADeviceType("MyDeviceTest"),
				new DeviceDetails("JustATest"),
				new RemoteService(
						new UDAServiceType("ContentDirectory"),
						new UDAServiceId("ContentDirectory"),
						URI.create("/dev/13131314-b0de-10e9-0000-0000171cd062/svc/upnp-org/ContentDirectory/desc.xml"),
						URI.create("/dev/13131314-b0de-10e9-0000-0000171cd062/svc/upnp-org/ContentDirectory/action"),
						URI.create("/dev/13131314-b0de-10e9-0000-0000171cd062/svc/upnp-org/ContentDirectory/event"),
						new Action[] { 
							new Action("Browse",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_BrowseFlag",ActionArgument.Direction.OUT)}),
							new Action("GetSortCapabilities",	new ActionArgument[] {new ActionArgument("Result", "SortCapabilities",ActionArgument.Direction.OUT)}),
							new Action("Search",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_SearchCriteria",ActionArgument.Direction.OUT)}),
							new Action("GetSystemUpdateID",	new ActionArgument[] {new ActionArgument("Result", "SystemUpdateID",ActionArgument.Direction.OUT)}),
							new Action("GetSearchCapabilities",	new ActionArgument[] {new ActionArgument("Result", "SearchCapabilities",ActionArgument.Direction.OUT)}),
							
							new Action("Filter",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_Filter",ActionArgument.Direction.OUT)}),
							new Action("ObjectID",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_ObjectID",ActionArgument.Direction.OUT)}),
							new Action("Count",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_Count",ActionArgument.Direction.OUT)}),
							new Action("Index",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_Index",ActionArgument.Direction.OUT)}),
							new Action("SortCriteria",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_SortCriteria",ActionArgument.Direction.OUT)}),
							new Action("URI",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_URI",ActionArgument.Direction.OUT)}),
							new Action("UpdateID",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_UpdateID",ActionArgument.Direction.OUT)}),
							new Action("Result",	new ActionArgument[] {new ActionArgument("Result", "A_ARG_TYPE_Result",ActionArgument.Direction.OUT)})
						},
						new StateVariable[] {
							new StateVariable("SortCapabilities",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_SearchCriteria",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("SystemUpdateID",new StateVariableTypeDetails(Datatype.Builtin.UI4.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("SearchCapabilities",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),							
							new StateVariable("A_ARG_TYPE_BrowseFlag",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),
							
							new StateVariable("A_ARG_TYPE_Filter",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_ObjectID",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_Count",new StateVariableTypeDetails(Datatype.Builtin.UI4.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_Index",new StateVariableTypeDetails(Datatype.Builtin.UI4.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_SortCriteria",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_URI",new StateVariableTypeDetails(Datatype.Builtin.URI.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_UpdateID",new StateVariableTypeDetails(Datatype.Builtin.UI4.getDatatype()), new StateVariableEventDetails(false)),
							new StateVariable("A_ARG_TYPE_Result",new StateVariableTypeDetails(Datatype.Builtin.STRING.getDatatype()), new StateVariableEventDetails(false))
						}
				)
			);
		
	}
	


}

