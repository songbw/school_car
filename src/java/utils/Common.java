package utils;

import java.util.* ;
import java.net.InetAddress ;
import java.net.NetworkInterface ;
import java.net.SocketException ;
import java.net.UnknownHostException ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.Graphics2D ;
import java.awt.image.BufferedImage ;
import java.io.ByteArrayInputStream ;
import java.io.ByteArrayOutputStream ;
import java.io.InputStream ;
import java.util.Map ;
import java.util.Random ;
import javax.imageio.ImageIO ;

public class Common {
    public static byte[] getMac() {
        byte[] mac = new byte[6] ;
        try {
            InetAddress inetAddr = InetAddress.getLocalHost();
            mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress() ;

        } catch (SocketException e) {
            e.printStackTrace() ;
        } catch (UnknownHostException e) {
            e.printStackTrace() ;
        }
        return mac ;
    }

    public static byte[] getMachineMacByIP() {
        try {
           /*
            * Above method often returns "127.0.0.1", In this case we need to
            * check all the available network interfaces
            */
            Enumeration<NetworkInterface> nInterfaces = NetworkInterface
                .getNetworkInterfaces();
            while (nInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = nInterfaces.nextElement() ;
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String address = inetAddresses.nextElement()
                        .getHostAddress();
                    if (!address.equals("127.0.0.1")) {
                        return networkInterface.getHardwareAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error = " + e.getMessage());
        }
        return null;
    }

    public static Color getRandomColor (int fc,int bc) throws Exception {
        Random random = new Random () ;
        if (fc > 255)
            fc = 200 ;
        if (bc > 255)
            bc = 255 ;
        int r = fc + random.nextInt (bc - fc) ;
        int g = fc + random.nextInt (bc - fc) ;
        int b = fc + random.nextInt (bc - fc) ;
        return new Color (r, g, b) ;
    }

    public static Map<String,byte []> getVerifyCode()throws Exception{
        int width = 70 ;
        int height = 40 ;
        BufferedImage image = new BufferedImage (width, height, BufferedImage.TYPE_3BYTE_BGR) ;
        Graphics2D g = image.createGraphics() ;
        Font font = new Font("Atlantic Inline", Font.BOLD, 30) ;
        g.setColor(getRandomColor (200, 250)) ;
        g.fillRect (0, 0, width, height) ;
        g.setFont (font) ;
        Random random = new Random () ;
        String code = "" ;
        for (int i=0; i<4; i++) {
            String str = "" ;
            String charOrNum = random.nextInt (2) % 2 == 0 ? "char" : "num" ;
            if ("char".equalsIgnoreCase (charOrNum)) {
                str = String.valueOf ((char) (97 + random.nextInt (26))) ;
                code += str ;
            } else if ("num".equalsIgnoreCase (charOrNum)) {
                str = String.valueOf (random.nextInt (10)) ;
                code += str ;
            }
            g.setColor (new Color (20 + random.nextInt (110), 20 +random.nextInt (110), 20 + random.nextInt (110))) ;
            g.drawString (str, 13 * i + 6, 30) ;
        }
        g.dispose () ;
        ByteArrayOutputStream output = new ByteArrayOutputStream () ;
        ImageIO.write (image, "JPEG", output) ;
        byte[] imageData = output.toByteArray() ;
        Map<String,byte []> map = new HashMap<String,byte []> () ;
        map.put (code,imageData) ;
//        System.out.println (code) ;
        return map ;
    }
}
