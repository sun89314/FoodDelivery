package com.example.fooddelivery.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.tea.TeaException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

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
		phoneNumber = "15021342284";
		Client client = SMSUtils.createClient("LTAI5tBN9LW2ZmrD6CzGKWkA", "yY1faZEKl4rGYoTIKivnSK8TfrGpB8");
		com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
				.setSignName("阿里云短信测试")
				.setTemplateCode("SMS_154950909")
				.setPhoneNumbers(phoneNumber)
				.setTemplateParam("{\"code\":\""+code+"\"}");
		com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
		try {
			// 复制代码运行请自行打印 API 的返回值
			com.aliyun.dysmsapi20170525.models.SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
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
		java.util.List<String> args = java.util.Arrays.asList(args_);
		// 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
		Client client = SMSUtils.createClient("LTAI5tBN9LW2ZmrD6CzGKWkA", "yY1faZEKl4rGYoTIKivnSK8TfrGpB8");
		com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
				.setSignName("阿里云短信测试")
				.setTemplateCode("SMS_154950909")
				.setPhoneNumbers("15021342284")
				.setTemplateParam("{\"code\":\"1234\"}");
		com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
		try {
			// 复制代码运行请自行打印 API 的返回值
			com.aliyun.dysmsapi20170525.models.SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
			System.out.println(response.getBody());
		} catch (TeaException error) {
			// 如有需要，请打印 error
			com.aliyun.teautil.Common.assertAsString(error.message);
		} catch (Exception _error) {
			TeaException error = new TeaException(_error.getMessage(), _error);
			// 如有需要，请打印 error
			com.aliyun.teautil.Common.assertAsString(error.message);
		}
	}

}
