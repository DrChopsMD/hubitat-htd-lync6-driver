# hubitat-htd-lync6-driver

Overview:

This project provides is based on the work of jmehlman (https://github.com/jmehlman/hubitat-htd-mca66-driver) and provides a basic driver interface for the Home Theatre Direct Lync6, assuming that you have the Lync6 connected to your network using their optional wired/wireless ethernet gateway.  I have thusfar been able to get the zones to turn on and off but other functionality is not ready yet.  The HTD hex commands that I have been using are directly from HTD and I have referenced below both the Lync and MCA systems.

Lync6:  https://www.htd.com/site/ownersmanual/lync_hex_codes.pdf

MCA66:  https://www.htd.com/site/ownersmanual/mca66_mc66_hex_codes.pdf

Installation:

Copy the interface and zone driver code into two separate files on your hubitat.  Create a new virtual device, and point it at the 'HTD Lync6 Amplifier Interface'.  Do NOT instantiate the 'HTD Lync6 Amplifier zone.' These will be automatically created by the Interface.  Edit the preferences pane to point at the IP address of your HTD gateway, and save the update.  You should be able to use the zones after that!

Open issues / future features:

State management at start-up is not great, but the driver should self- correct.  I have not been able to get the volume control, source change or zone change to work.

Bugs / other issues:

Feel free to publish bugs, improvements, or other issues here!
