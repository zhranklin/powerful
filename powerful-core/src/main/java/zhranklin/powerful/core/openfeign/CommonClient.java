package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhranklin.powerful.model.Instruction;

import java.util.Map;

public interface CommonClient<T> {
    @PostMapping(value = {"{path}/execute"})
    public ResponseEntity<String> executePost(@RequestBody Instruction instruction, @RequestHeader Map<String, String> headers, @RequestParam Map<String, String> params, @PathVariable("path") String path);

    @PutMapping(value = {"{path}/execute"})
    public ResponseEntity<String> executePut(@RequestBody Instruction instruction, @RequestHeader Map<String, String> headers, @RequestParam Map<String, String> params, @PathVariable("path") String path);

    @GetMapping(value = {"{path}/execute"})
    public ResponseEntity<String> executeGet(@RequestParam("_body") String _body, @RequestHeader Map<String, String> headers, @RequestParam Map<String, String> params, @PathVariable("path") String path);

    @DeleteMapping(value = {"{path}/execute"})
    public ResponseEntity<String> executeDelete(@RequestParam("_body") String _body, @RequestHeader Map<String, String> headers, @RequestParam Map<String, String> params, @PathVariable("path") String path);
}
