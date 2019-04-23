package com.pinyougou.manager.controller;

import com.pinyougou.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
@RequestMapping("/updates")
public class UpdateConstroller {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("/add")
    public Result add(MultipartFile file)  {
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(i + 1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String s = fastDFSClient.uploadFile(file.getBytes(), substring);
            String url=FILE_SERVER_URL+s;
            System.out.println(url);
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"上传失败");
        }

    }
}
