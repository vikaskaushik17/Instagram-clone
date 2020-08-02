package com.vikaskaushik.instagramclone.Utils;

public class StringManipulation {

    public static String expandUsername(String username) {
        return username.replace(".", " ");
    }

    public static String condenseUsername(String username) {
        return username.replace(" ", ".");
    }

    public static String getTags(String string) {
        if (string.indexOf("#") > 0) {
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundArray = false;
            for (char c : charArray) {
                if (c == '#') {
                    foundArray = true;
                    sb.append(c);
                } else {
                    if (foundArray) {
                        sb.append(c);
                    }
                }
                if (c == ' ') {
                    foundArray = false;
                }
            }
            String s = sb.toString().replace(" ", "").replace("#", ",#");
            return s.substring(1, s.length());
        }
        /**
         * In -> some description #tag1 tag2 #other tag
         *
         * out -> #tag1, #tag2, #tag3
         */
        return string;
    }
}
