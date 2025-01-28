package io.axoniq.demo.paymentmodule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentDetailsEvent;
import io.axoniq.demo.bikerental.coreapi.payment.PaymentStatus;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final ObjectMapper objectMapper;
    private final PaymentStatusRepository paymentStatusRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    public PaymentController(ObjectMapper objectMapper, PaymentStatusRepository paymentStatusRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.paymentStatusRepository = paymentStatusRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/getPaymentReference")
    public ResponseEntity<String> paymentReference(@RequestParam(value = "bikeId") String bikeId) {
        PaymentStatus paymentStatus = paymentStatusRepository.findByBikeId(bikeId);
        if (paymentStatus == null) {
            throw new ResourceNotFoundException("Payment reference not found");
        }
        return new ResponseEntity<>(paymentStatus.getReference(), HttpStatus.OK);
    }

    @PostMapping("/confirmPayment")
    public void confirmPayment(@RequestParam(value = "paymentId") String paymentId,
                               @RequestParam(value = "confirmStatus") Boolean confirmStatus) throws JsonProcessingException {
        PaymentStatus paymentStatus = paymentStatusRepository.findByReference(paymentId);
        if (paymentStatus == null) {
            throw new ResourceNotFoundException("Payment with this reference does not exist");
        }
        if (confirmStatus != null && confirmStatus) {
            paymentStatus.setStatus(PaymentStatus.Status.APPROVED);
            paymentStatusRepository.save(paymentStatus);
            PaymentDetailsEvent paymentDetailsEvent = new PaymentDetailsEvent(paymentStatus.getBikeId(), paymentId, PaymentDetailsEvent.PaymentApproveStatus.APPROVED);
            String paymentEvent = objectMapper.writeValueAsString(paymentDetailsEvent);
            kafkaTemplate.send("payment-details", paymentEvent);
        } else {
            paymentStatus.setStatus(PaymentStatus.Status.REJECTED);
            paymentStatusRepository.save(paymentStatus);
            PaymentDetailsEvent paymentDetailsEvent = new PaymentDetailsEvent(paymentStatus.getBikeId(),paymentId, PaymentDetailsEvent.PaymentApproveStatus.FAILED);
            String paymentEvent = objectMapper.writeValueAsString(paymentDetailsEvent);
            kafkaTemplate.send("payment-details", paymentEvent);
        }
    }
}
