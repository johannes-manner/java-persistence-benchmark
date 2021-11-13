package de.uniba.dsg.wss.api.jpa;

import de.uniba.dsg.wss.api.TransactionsController;
import de.uniba.dsg.wss.data.transfer.messages.*;
import de.uniba.dsg.wss.service.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * This controller provides access to the services of the server when launched in JPA persistence
 * mode.
 *
 * @author Benedikt Full
 */
@RestController
@ConditionalOnProperty(name = "jpb.persistence.mode", havingValue = "jpa")
public class JpaTransactionsController implements TransactionsController {

  private final JpaNewOrderService newOrderService;
  private final JpaPaymentService paymentService;
  private final JpaOrderStatusService orderStatusService;
  private final JpaDeliveryService deliveryService;
  private final JpaStockLevelService stockLevelService;

  @Autowired
  public JpaTransactionsController(
      JpaNewOrderService newOrderService,
      JpaPaymentService paymentService,
      JpaOrderStatusService orderStatusService,
      JpaDeliveryService deliveryService,
      JpaStockLevelService stockLevelService) {
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
    LocalDateTime startTime = LocalDateTime.now();
    NewOrderResponse newOrderResponse = newOrderService.process(req);
    LocalDateTime endTime = LocalDateTime.now();

    return ResponseEntity.ok().headers(this.generateCustomHeaders(startTime, endTime)).body(newOrderResponse);
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
    DeliveryResponse deliveryResponse = deliveryService.process(req);
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
