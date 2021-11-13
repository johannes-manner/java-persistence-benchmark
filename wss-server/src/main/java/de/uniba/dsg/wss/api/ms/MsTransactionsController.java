package de.uniba.dsg.wss.api.ms;

import de.uniba.dsg.wss.api.TransactionsController;
import de.uniba.dsg.wss.data.transfer.messages.*;
import de.uniba.dsg.wss.service.ms.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * This controller provides access to the services of the server when launched in MS persistence
 * mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "ms")
public class MsTransactionsController implements TransactionsController {

  private final MsNewOrderService newOrderService;
  private final MsPaymentService paymentService;
  private final MsOrderStatusService orderStatusService;
  private final MsDeliveryService deliveryService;
  private final MsStockLevelService stockLevelService;

  public MsTransactionsController(MsNewOrderService newOrderService,
      MsPaymentService paymentService,
      MsOrderStatusService orderStatusService,
      MsDeliveryService deliveryService,
      MsStockLevelService stockLevelService) {
    this.newOrderService = newOrderService;
    this.paymentService = paymentService;
    this.orderStatusService = orderStatusService;
    this.deliveryService = deliveryService;
    this.stockLevelService = stockLevelService;
  }

  private HttpHeaders generateCustomHeaders(LocalDateTime startTime, LocalDateTime endTime) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(REQUEST_DURATION, "" + Duration.between(startTime, endTime).toMillis());
    return responseHeaders;
  }

  @Override
  public ResponseEntity<NewOrderResponse> doNewOrderTransaction(NewOrderRequest req) {
    try {
      LocalDateTime startTime = LocalDateTime.now();
      NewOrderResponse newOrderResponse = newOrderService.process(req);
      LocalDateTime endTime = LocalDateTime.now();

      return ResponseEntity.ok().headers(this.generateCustomHeaders(startTime, endTime)).body(newOrderResponse);
    } catch(MsTransactionException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @Override
  public ResponseEntity<PaymentResponse> doPaymentTransaction(PaymentRequest req) {
    LocalDateTime startTime = LocalDateTime.now();
    PaymentResponse paymentResponse = paymentService.process(req);
    LocalDateTime endTime = LocalDateTime.now();

    return ResponseEntity.ok().headers(this.generateCustomHeaders(startTime,endTime)).body(paymentResponse);
  }

  @Override
  public ResponseEntity<OrderStatusResponse> doOrderStatusTransaction(OrderStatusRequest req) {
    LocalDateTime startTime = LocalDateTime.now();
    OrderStatusResponse orderStatusResponse = orderStatusService.process(req);
    LocalDateTime endTime = LocalDateTime.now();

    return ResponseEntity.ok().headers(this.generateCustomHeaders(startTime,endTime)).body(orderStatusResponse);
  }

  @Override
  public ResponseEntity<DeliveryResponse> doDeliveryTransaction(DeliveryRequest req) {
    LocalDateTime startTime = LocalDateTime.now();
    DeliveryResponse deliveryResponse =  deliveryService.process(req);
    LocalDateTime endTime = LocalDateTime.now();

    return ResponseEntity.ok().headers(this.generateCustomHeaders(startTime,endTime)).body(deliveryResponse);
  }

  @Override
  public ResponseEntity<StockLevelResponse> doStockLevelTransaction(StockLevelRequest req) {
    LocalDateTime startTime = LocalDateTime.now();
    StockLevelResponse stockLevelResponse = stockLevelService.process(req);
    LocalDateTime endTime = LocalDateTime.now();

    return ResponseEntity.ok().headers(this.generateCustomHeaders(startTime,endTime)).body(stockLevelResponse);
  }
}
