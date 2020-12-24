/**
 *  HTD LYNC6 Amplifier Interface
 *
 *  Copyright 2020 DrChops
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.

 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *   
 *   The code below is based off the code written by jmehlman (https://github.com/jmehlman/hubitat-htd-mca66-driver)
 *
 *   Change Log:
 *     12/21/2020 v1.0 - Initial release
 *     12/22/2020 v1.1 - added mute & input select
 *
 */
metadata {
    definition(name: "HTD LYNC6 Amplifier Interface", namespace: "htdlync6", author: "DrChops") {
        command "sendTestMessage"

        command "volumeUp", [[name:"zone",type:"NUMBER", description:"Zone #1-6", constraints:["NUMBER"]]]
        command "volumeDown", [[name:"zone",type:"NUMBER", description:"Zone #1-6", constraints:["NUMBER"]]]
        command "createZones"
        command "deleteZones"

        capability "HealthCheck"
        //capability "AudioVolume"

    }

    preferences{
        input name: 'ipAddress', type: 'text', title: 'IP Address', required: true, defaultValue: '10.0.0.53', description: 'IP Address for Gateway'
        input name: 'port', type: 'number', title: 'IP Port', required: true, defaultValue: 10006, description: 'IP Port for Gateway'
        input name: 'Source1Name', type: 'text', title: 'Source 1 Name', required: true, defaultValue: 'Source 1', description: 'Name for Source 1'
        input name: 'Source2Name', type: 'text', title: 'Source 2 Name', required: true, defaultValue: 'Source 2', description: 'Name for Source 2'
        input name: 'Source3Name', type: 'text', title: 'Source 3 Name', required: true, defaultValue: 'Source 3', description: 'Name for Source 3'
        input name: 'Source4Name', type: 'text', title: 'Source 4 Name', required: true, defaultValue: 'Source 4', description: 'Name for Source 4'
        input name: 'Source5Name', type: 'text', title: 'Source 5 Name', required: true, defaultValue: 'Source 5', description: 'Name for Source 5'
        input name: 'Source6Name', type: 'text', title: 'Source 6 Name', required: true, defaultValue: 'Source 6', description: 'Name for Source 6'
        input name: 'input7Name', type: 'text', title: 'input 7 Name', required: true, defaultValue: 'input 7', description: 'Name for input 7'
        input name: 'input8Name', type: 'text', title: 'input 8 Name', required: true, defaultValue: 'input 8', description: 'Name for input 8'
        input name: 'input9Name', type: 'text', title: 'input 9 Name', required: true, defaultValue: 'input 9', description: 'Name for input 9'
        input name: 'input10Name', type: 'text', title: 'input 10 Name', required: true, defaultValue: 'input 10', description: 'Name for input 10'
        input name: 'input11Name', type: 'text', title: 'input 11 Name', required: true, defaultValue: 'input 11', description: 'Name for input 11'
        input name: 'input12Name', type: 'text', title: 'input 12 Name', required: true, defaultValue: 'input 12', description: 'Name for input 12'
    }
}

void configure() {}

void installed() {
    createZones()
}

void volumeUp(zone) {
    if (zone<1 || zone>6)
    {
        log.error "Invalid Zone"
        return
    }
    else
    {

        def cmd = [0x02, 0x00, zone, 0x04, 0x09] as byte[]
        sendMessage(cmd)
    }
}

void volumeDown(zone) {
    if (zone<1 || zone>6)
    {
        log.error "Invalid Zone"
        return
    }
    else
    {

        def cmd = [0x02, 0x00, zone, 0x04, 0x0A] as byte[]
        sendMessage(cmd)
    }
}

void on(byte zone) {
    def msg = [2,0,zone,4,0x57] as byte[]
    sendMessage(msg)
}

void off(byte zone) {
    def msg = [2,0,zone,4,0x58] as byte[]
    sendMessage(msg)
}

void sendTestMessage() {
    def msgon = [2,0,5,4,0x20] as byte[]

    sendMessage(msgon)
}

void Mute(zone) {
    def msg = [0x02,0x00,zone,0x04,0x1E] as byte[]

    sendMessage(msg)
}

void Unmute(zone) {
    def msg = [0x02,0x00,zone,0x04,0x1F] as byte[]

    sendMessage(msg)
}

void selectInput(zone, byte inputNum) {
    def inputNumRange = 1..6
    if ( inputNumRange.contains(inputNum as int) )
    {
        def msg = [0x02, 0x00, zone, 0x04, 0x10] as byte[]
        sendMessage(msg)
    }
    else {
        log.error "Invalid input number: ${inputNum}"
    }
}


void createZones() {
    for (i in 1..6)
    {
       cd = addChildDevice("htdlync6", "HTD LYNC6 Amplifier Zone", "${device.deviceNetworkId}-ep${i}", [name: "${device.displayName} (Zone${i})", isComponent: true])
       cd.setZone(i)
    }
}

void deleteZones() {
    zones = getChildDevices()
    log.debug "${zones}"
    for (i in 1..6) {
        deleteChildDevice("${device.deviceNetworkId}-ep${i}")
    }
}

/*******************
**   Message API  **
********************/
void sendMessage(byte[] byte_message) {
    def ip = ipAddress as String
    def p = port as int

    // calculate checksum
    def cksum = [0] as byte[]
    for (byte i : byte_message)
    {
        cksum[0] += i
    }
    log.debug "Cksum computed as: ${cksum}"

    def msg_cksum = [byte_message, cksum].flatten() as byte[]

    def strmsg = hubitat.helper.HexUtils.byteArrayToHexString(msg_cksum)

    log.debug "Sending Message: ${strmsg} to ${ipAddress}:${port}"

    interfaces.rawSocket.connect(ip, p, 'byteInterface':true)
    interfaces.rawSocket.sendMessage(strmsg)

    //interfaces.rawSocket.close()
}

void receiveMessage(byte[] byte_message)
{
    def PACKET_SIZE = 14 // all packets should be 14 bytes

    log.debug "Received Message: ${byte_message}, length ${byte_message.length}"

    // iterate over packets
    for (int i = 0; i < byte_message.length; i+=PACKET_SIZE)
    {
        //log.debug "Decoding Packet #${i/PACKET_SIZE}"
        def header = [2, 0] as byte[]
        if (byte_message[i..i+1] != header) {
            log.debug "Invalid value"
            continue
        }

        zone = byte_message[i+2]

        // Command should be 0x05
        if (byte_message[3] != 0x05) {
            log.warn "Unknown packet type"
        }

        def d1 = byte_message[4] as byte
        boolean powerOn = (d1 >> 7 & 0x01)
        def poweris = 'off'
        if(powerOn) poweris = 'on'

        boolean mute = d1 >> 6 & 0x01
        def muteIs = 'muted'

        if(!mute) muteIs = 'unmuted'

        def input = byte_message[8]+1
        def volume = byte_message[9]+60 as int

        def volumePercentage = volume*100/60

        log.debug "Zone: ${zone}, power: ${powerOn}, mute = ${mute}, input = ${input}, volume = ${volume}"

        // Put in state map for update
        def zoneStates = ['switch' : poweris, 'mute' : muteIs, 'volume' : volumePercentage, 'inputNumber' : input]
        getChildDevice("${device.deviceNetworkId}-ep${zone}").updateState(zoneStates)


    }
}

// Asynchronous receive function
void parse(String msg) {
    receiveMessage(hubitat.helper.HexUtils.hexStringToByteArray(msg))
}
