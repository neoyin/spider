package com.lifeix.bed.client;

import com.lifeix.com.lifeix.utils.JSONUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by neoyin on 16/9/28.
 */
public class BedClientUtils {


    private static String BEDSERVER_PATH ="";


    public static void init(String path){
        BEDSERVER_PATH =  path;
//		ClientConfig cc = new DefaultClientConfig();
//		cc.getClasses().add(MultiPartWriter.class);
//		client = Client.create(cc);
    }

    private static Client getClient(){
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(MultiPartWriter.class);
        Client client = Client.create(cc);
        return client;
    }

    /***
     * 数据签名
     *
     * @param map
     * @return
     */
    public static String getSignature(Map<String, String> map) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                list.add(entry.getKey() + "=" + entry.getValue());
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
            if (i != size - 1) {
                sb.append("&");
            }
        }
        String result = sb.toString();
        result = DigestUtils.md5Hex(result);
        return result;
    }

    public static BedImagePO uploadImg(File file) throws JSONException, IOException {

        FormDataMultiPart part = new FormDataMultiPart();
        //part.field("file", file, MediaType.MULTIPART_FORM_DATA_TYPE);
        part.bodyPart(new FileDataBodyPart("file", file, MediaType.MULTIPART_FORM_DATA_TYPE));
        Map<String, String> map=new HashMap<String,String>();
        map.put("fileName", file.getName());
        String signCheck=getSignature(map);
        part.field("sign",signCheck);
        Client client = getClient();
        WebResource webResource = client.resource(BEDSERVER_PATH+"wxcity/image");
        String response= webResource.type(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON).post(String.class,part);

        JSONObject object = new JSONObject(response);
         int code =  object.getInt("code");
        if (code!=1000)return null;
        String data = object.getString("data");
        BedImagePO image = JSONUtils.json2pojo(data,BedImagePO.class);

        //System.out.println(data);

        return image;
    }

    /**
     * 标题                  title           String            长度256          必填
     内容                  content    String            长度15100       必填
     附加参数           params     String            json格式（参考特殊说明部分） 非必填
     类别                  type          Long             1 图文新闻（或文字新闻）  2 视频新闻   必填
     文章发表时间     time         String            必填
     数字签名加密串  sign         String            必填
     */
    public static String sendArticle(String title,String content,String params,int type){

        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
        String time = format.format(new Date());

        Form form =  new Form();
        form.add("title", title);
        form.add("content",content);
        form.add("params", params);
        form.add("type",String.valueOf(type));
        form.add("time",time);


        Map<String, String> map=new HashMap<String,String>();
        map.put("title", title);
        map.put("content", content);
        map.put("params", params);
        map.put("type",String.valueOf(type));
        map.put("time", time);
        String sign = getSignature(map);
        System.out.println(sign+"---"+time);
        form.add("sign", sign);

        Client client = getClient();
        WebResource webResource = client.resource(BEDSERVER_PATH+"wxcity/in");
        String response= webResource.type(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).post(String.class,form);
        System.out.println(response);
        return response;
    }

    public static void main(String[] args) throws JSONException, IOException {
        BedClientUtils.init("http://192.168.2.163:8080/");
        File file =new File("/Volumes/KSSD/data/db/spider/imgs/0f37ba02-46fa-4ee5-a251-722e83e8768b_IMG4ccc6a3d1b8f42133819662.jpg");BedClientUtils.uploadImg(file);


        String title ="男子不堪忍受债主讨要赌资 点火自残(组图)";
        String content ="${lx-image-0} ${lx-br}${lx-image-1} ${lx-br}${lx-image-2} ${lx-br}前天中午，一名男子不堪忍受债主三番五次前来讨要赌资，竟然将煤气罐搬到卧室，拧开煤气罐阀门点火后自焚。爆炸声惊动了闻讯赶来的邻居，并立即将其烧成重伤的男子送往医院抢救。目前，自焚男子仍然没有脱离生命危险。事发下午一点。 ${lx-br}正在家中午休的六合龙池街道刘营村的村民突然听到附近传来轰的一声巨响，邻居们不知怎么回事，纷纷跑出门外查看究竟，原来爆炸声是从邻居郁某家传来的，当邻居跑到郁某家门口时，郁某家二楼窗户仍然在往外边冒黑烟，邻居发现一个黑人从郁某家楼后面慌慌张张跑了出来，刚刚跑出来没几米，整个全身黑乎乎的人一下子栽倒在地上。不醒人事，邻居跑过去蹬下来一看，发现这个满身黑乎乎的人竟然是这个家的主人郁某。邻居赶忙报警。很快，当地110,120,119急救处理和人员赶到。 ${lx-br}120急救人员上前检查后，发现男子全身多处被烧伤，当男子还有呼吸。医护人员立即将其抬上救护车，送往六合人民医院抢救。119消防官兵架起水枪冲上二楼房间将房间遗留的火苗扑灭，民警在楼下菜园里10米远方的地方发现两个被炸毁飞出去二楼铝合金窗户。玻璃碎片满地都是，二楼房间爆炸现场，民警还发现一个打开阀门的煤气罐，旁边还有一个破碎的打火机残片，整个房间物品被烧毁差不多。郁某邻居告诉记者，郁某今年40几岁，现在独居生活，20年前刚结婚不久就与妻子离婚，两年前，经过朋友介绍，于贵州一名女子认识并结婚，可是，贵州妻子结婚没几天就带着贵重钱物离开他，不知去向。这对郁某精神打击太大。不久，郁某就沾上了赌博，两年时间，郁某欠下10万元赌资。 ${lx-br}这几天，债主几乎天天上门讨要赌债，由于郁某靠打工生活，没有经济来源，为了躲债，郁某四处借钱，朋友知道他好赌，都不敢再借给他，这让郁某很郁闷，这几天，在邻居面前不知一次流露出活着没意思，不知一死百了，邻居们以为他是在开玩笑，没有当回事。 ${lx-br}想不到，郁某中午突然做出这个自焚举动。记者赶到六合人民医院急诊室时，医生告诉记者，郁某烧伤面积过大，刚刚转往南京一家专门收治烧伤患者的大医院急救。目前，当地警方已经赶到南京收治郁某的医院，对男子为何要自焚的原因展开调查。 ${lx-br}";

        List<BedImagePO> poList = new ArrayList<>();
        BedImagePO po = new BedImagePO("0","0","http://img1.gtimg.com/17/1767/176710/17671009.png");
        poList.add(po);
        String params = JSONUtils.obj2json(poList);
        System.out.println(params);
        BedClientUtils.sendArticle(title,content,params,1);



    }




}
