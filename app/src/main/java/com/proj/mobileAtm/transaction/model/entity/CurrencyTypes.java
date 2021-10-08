package com.proj.mobileAtm.transaction.model.entity;

public class CurrencyTypes {

    public static class FiveHundredRupeeNote extends Notes {

        private static final int VALUE = 500;

        public FiveHundredRupeeNote(int available) {
            super(available, VALUE);
        }

        public String getType() {
            return "FIVEHUNDRED";
        }
    }

    public static class HundredRupeeNote extends Notes {

        private static final int VALUE = 100;

        public HundredRupeeNote(int available) {
            super(available, VALUE);
        }

        public String getType() {
            return "HUNDRED";
        }
    }

    public static class TwoThousandRupeeNote extends Notes {

        private static final int VALUE = 2000;

        public TwoThousandRupeeNote(int available) {
            super(available, VALUE);
        }

        public String getType() {
            return "TWOTHOUSAND";
        }
    }
}
