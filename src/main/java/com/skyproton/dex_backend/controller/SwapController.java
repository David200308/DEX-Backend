package com.skyproton.dex_backend.controller;

import com.skyproton.dex_backend.dto.swap.ReqSwapDTO;
import com.skyproton.dex_backend.service.SwapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/swap")
public class SwapController {
    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @PostMapping
    public ResponseEntity<?> swap(@RequestBody ReqSwapDTO reqSwapDTO) {
        return null;
    }

}
