package com.example.demo.services;

import com.example.demo.objects.PinataUploadResult;

public interface PinataClient {
    PinataUploadResult uploadJson(String fileName, String jsonContent);
}

