package com.example.fooddelivery.Controller;

import com.example.fooddelivery.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info(file.toString());
        //原始文件名
        String originalFileName = file.getOriginalFilename();
        //使用UUID重新生成文件名
        String suffix = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + suffix;
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        // 输入流，读取文件内容
        try {
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            ServletOutputStream os = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fis.read(bytes)) != -1 ){
                os.write(bytes,0,len);
                os.flush();
            }
            os.close();
            fis.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //输出流，将文件传给浏览器

    }
}
