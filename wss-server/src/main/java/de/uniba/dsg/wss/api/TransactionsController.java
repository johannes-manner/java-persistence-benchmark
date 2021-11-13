package de.uniba.dsg.wss.api;

import de.uniba.dsg.wss.data.transfer.messages.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Implementations of this controller allow clients to interact with the services of this server.
 * These services are the implementations of the business transactions.
 *
 * @author Benedikt Full
 */
@RequestMapping("api")
@Validated
public interface TransactionsController {

  String REQUEST_DURATION = "requestDuration";

  @PostMapping(value = "transactions/new-order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<NewOrderResponse> doNewOrderTransaction(@Valid @RequestBody NewOrderRequest req);

  @PostMapping(value = "transactions/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<PaymentResponse> doPaymentTransaction(@Valid @RequestBody PaymentRequest req);

  @GetMapping(value = "transactions/order-status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<OrderStatusResponse> doOrderStatusTransaction(@Valid @RequestBody OrderStatusRequest req);

  @PutMapping(value = "transactions/delivery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<DeliveryResponse> doDeliveryTransaction(@Valid @RequestBody DeliveryRequest req);

  @GetMapping(value = "transactions/stock-level", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<StockLevelResponse> doStockLevelTransaction(@Valid @RequestBody StockLevelRequest req);
}
