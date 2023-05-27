package com.example.fooddelivery.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.tea.TeaException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
		}catch (ClientException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 使用AK&SK初始化账号Client
	 * @param accessKeyId
	 * @param accessKeySecret
	 * @return Client
	 * @throws Exception
	 */
	public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
		com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
				// 必填，您的 AccessKey ID
				.setAccessKeyId(accessKeyId)
				// 必填，您的 AccessKey Secret
				.setAccessKeySecret(accessKeySecret);
		// 访问的域名
		config.endpoint = "dysmsapi.aliyuncs.com";
		return new com.aliyun.dysmsapi20170525.Client(config);
	}
	public static void sendMsg(String phoneNumber, String code) throws Exception{
		Client client = SMSUtils.createClient("LTAI5tBN9LW2ZmrD6CzGKWkA", "yY1faZEKl4rGYoTIKivnSK8TfrGpB8");
		com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
				.setSignName("瑞吉外卖")
				.setTemplateCode("SMS_460895241")
				.setPhoneNumbers(phoneNumber)
				.setTemplateParam("{\"code\":\""+code+"\"}");
		com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
		try {
			// 复制代码运行请自行打印 API 的返回值
			com.aliyun.dysmsapi20170525.models.SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
			System.out.println(response);
		} catch (TeaException error) {
			// 如有需要，请打印 error
			com.aliyun.teautil.Common.assertAsString(error.message);
		} catch (Exception _error) {
			TeaException error = new TeaException(_error.getMessage(), _error);
			// 如有需要，请打印 error
			com.aliyun.teautil.Common.assertAsString(error.message);

		}
	}
	public static void main(String[] args_) throws Exception {
		//设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//初始化ascClient需要的几个参数
		final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
//替换成你的AK
		final String accessKeyId = "LTAI5tBN9LW2ZmrD6CzGKWkA";//你的accessKeyId,参考本文档步骤2
		final String accessKeySecret = "yY1faZEKl4rGYoTIKivnSK8TfrGpB8";//你的accessKeySecret，参考本文档步骤2
//初始化ascClient,暂时不支持多region（请勿修改）
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
				accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);
		//组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		//使用post提交
		request.setMethod(MethodType.POST);
		//必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
		request.setPhoneNumbers("13816334019");
		//必填:短信签名-可在短信控制台中找到
		request.setSignName("瑞吉外卖");
		//必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
		request.setTemplateCode("SMS_460895241");
		//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		//友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
//参考：request.setTemplateParam("{\"变量1\":\"值1\",\"变量2\":\"值2\",\"变量3\":\"值3\"}")
		String code = "111111";
		request.setTemplateParam("{\"code\":\""+code+"\"}");
		//可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
		//request.setSmsUpExtendCode("90997");
		//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		request.setOutId("yourOutId");
//请求失败这里会抛ClientException异常
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
//请求成功
		}

	}

}
