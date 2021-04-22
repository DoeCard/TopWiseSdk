package com.doe.topwisesdk.domain.doecard.samndfelica.felica;

/**
 * Created by eshantmittal on 16/01/18.
 */

public class ServiceCodes {






    public class CardSpecificData {

        //Service 3008 random read/write (with security)
        private byte[] srvCodeCardSpecificDataEnc = new byte[]{
                (byte) 0x08,
                (byte) 0x30
        };

        //Service 300B random read only (non-security)
        private byte[] srvCodeCardSpecificDataWOEnc = new byte[]{
                (byte) 0x0B,
                (byte) 0x30
        };


        public byte[] getSrvCodeCardSpecificDataEnc() {
            return srvCodeCardSpecificDataEnc;
        }

        public byte[] getSrvCodeCardSpecificDataWOEnc() {
            return srvCodeCardSpecificDataWOEnc;
        }
    }


    public class TollData {

        //Service 4008 random read/write (with security)
        private byte[] srvCodeTollRandomEnc = new byte[]{
                (byte) 0x08,
                (byte) 0x40
        };

        //Service 404C cyclic read/write (with security)
        private byte[] srvCodeTollCyclicEnc = new byte[]{
                (byte) 0x4C,
                (byte) 0x40
        };

        public byte[] getSrvCodeTollRandomEnc() {
            return srvCodeTollRandomEnc;
        }

        public byte[] getSrvCodeTollCyclicEnc() {
            return srvCodeTollCyclicEnc;
        }
    }

    public class RailwayData {

        //Service 4088 random read/write (with security)
        private byte[] srvCodeRailwayRandomEnc = new byte[]{
                (byte) 0x88,
                (byte) 0x40
        };

        //Service 40CC cyclic read/write (with security)
        private byte[] srvCodeRailwayCyclicEnc = new byte[]{
                (byte) 0xCC,
                (byte) 0x40
        };

        public byte[] getSrvCodeRailwayRandomEnc() {
            return srvCodeRailwayRandomEnc;
        }

        public byte[] getSrvCodeRailwayCyclicEnc() {
            return srvCodeRailwayCyclicEnc;
        }
    }

    public class MetroData {

        //Service 4108 random read/write (with security)
        private byte[] srvCodeMetroRandomEnc = new byte[]{
                (byte) 0x08,
                (byte) 0x41
        };

        public byte[] getSrvCodeMetroRandomEnc() {
            return srvCodeMetroRandomEnc;
        }
    }

    public class BusData {

        //Service 4148 random read/write (with security)
        private byte[] srvCodeBusRandomEnc = new byte[]{
                (byte) 0x48,
                (byte) 0x41
        };

        public byte[] getSrvCodeBusRandomEnc() {
            return srvCodeBusRandomEnc;
        }
    }

    public class ParkingData {

        //Service 4188 random read/write (with security)
        private byte[] srvCodeParkingRandomEnc = new byte[]{
                (byte) 0x88,
                (byte) 0x41
        };

        public byte[] getSrvCodeParkingRandomEnc() {
            return srvCodeParkingRandomEnc;
        }
    }

}
