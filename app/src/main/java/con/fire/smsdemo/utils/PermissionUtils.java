package con.fire.smsdemo.utils;

import android.app.Activity;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    public static boolean checkPermissionRationale(Activity context, String[] perm) {

        for (String item : perm) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, item)) {
                return true;
            }
        }
        return false;
    }
//    public boolean checkPermission(Context context, String[] perm) {
////--------------------------j junk code--start---------------------------
//        try {
//            // createStringBuilderCodeTemplate
//            StringBuilder vEszMbpWqk = new StringBuilder();
//            String vKryFulJdk = "1,1oo1,1ii8,8t-td,daa-,-ss4,4uu";
//            String vZvlJnqXkf = "g,gggy,yxxb,b.-.8,8";
//            vEszMbpWqk.append(vKryFulJdk);
//            boolean vJewRmdRma = vEszMbpWqk.toString().equals(vKryFulJdk);
//            if(!vJewRmdRma) {
//                vEszMbpWqk.append(vZvlJnqXkf);
//            }
//        } catch (Exception jCode201e) {
//            jCode201e.printStackTrace();
//        }
////--------------------------j junk code--end-----------------------------
//
//        for (String item : perm) {
//            if (ActivityCompat.checkSelfPermission(context,
//                    item) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//    public boolean checkGrantResults(int[] grantResults) {
////--------------------------j junk code--start---------------------------
//        try {
//            // createArrayListCodeTemplate
//            String[] vXlmOvsVrg = {"PerVbjUxkH", "XhrKdaD", "BduDft"};
//            List<String> vScmShiLtm = new ArrayList<>(vXlmOvsVrg.length);
//            for (String vYohSwpZmg : vXlmOvsVrg) {
//                vScmShiLtm.add(vYohSwpZmg);
//            }
//            Map<String, Object> vXouBgiTio = new HashMap<>();
//            for (int vLdtXdfHsw = 0; vLdtXdfHsw < vScmShiLtm.size(); vLdtXdfHsw++) {
//                String vYdbWhnLhc = vScmShiLtm.get(vLdtXdfHsw);
//                vXouBgiTio.put(vYdbWhnLhc, "vYdbWhnLhcVslCciPsj");
//            }
//        } catch (Exception jCode353e) {
//            jCode353e.printStackTrace();
//        }
////--------------------------j junk code--end-----------------------------
//
//        for (int code : grantResults) {
//            if (code != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
}
