package com.example.swinecore.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class QrCodeUtil {

    @Value("${app.qr-secret:SwineCoreDefaultSecret2025!}")
    private String appSecret;

    /** Generate SHA-256 HMAC signature for order verification. */
    public String generateSignature(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate QR signature", e);
        }
    }

    /** Verify a SHA-256 HMAC signature against a payload. */
    public boolean verifySignature(String payload, String expectedSignature) {
        String computed = generateSignature(payload);
        return expectedSignature != null && MessageDigest.isEqual(
            computed.getBytes(StandardCharsets.UTF_8),
            expectedSignature.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a QR code PNG as a Base64-encoded string (for inline img src).
     */
    public String generateBase64QrCode(String content, int width, int height)
            throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 2);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Build the QR content string for an order with SHA-256 signature.
     */
    public String buildOrderQrContent(String baseUrl, String orderReference,
                                       String orderType, String buyerName,
                                       double amount, String details) {
        String payload = String.format(
            "ORDER_REF=%s|TYPE=%s|BUYER=%s|AMOUNT=%.2f|DETAILS=%s|URL=%s/checkout/confirm/%s",
            orderReference, orderType, buyerName, amount, details, baseUrl, orderReference
        );
        String signature = generateSignature(payload);
        return payload + "|SIG=" + signature;
    }

    /** Build a customer-scannable payment URL whose invoice fields are HMAC signed. */
    public String buildCustomerPaymentUrl(String baseUrl, String orderReference,
                                          String orderType, double amount,
                                          String farm, String details) {
        String amountText = String.format(Locale.ROOT, "%.2f", amount);
        String payload = paymentPayload(orderReference, orderType, amountText, farm, details);
        return UriComponentsBuilder.fromUriString(baseUrl)
            .path("/checkout/scan")
            .queryParam("ref", orderReference)
            .queryParam("type", orderType)
            .queryParam("amount", amountText)
            .queryParam("farm", farm)
            .queryParam("details", details)
            .queryParam("sig", generateSignature(payload))
            .build()
            .encode()
            .toUriString();
    }

    public boolean verifyCustomerPayment(String orderReference, String orderType,
                                         String amount, String farm, String details,
                                         String signature) {
        return verifySignature(paymentPayload(orderReference, orderType, amount, farm, details), signature);
    }

    private String paymentPayload(String reference, String type, String amount,
                                  String farm, String details) {
        return String.join("|", reference, type, amount, farm, details);
    }

    /** Parse the signature from a QR content string. */
    public String extractSignature(String qrContent) {
        if (qrContent == null || !qrContent.contains("|SIG=")) return null;
        return qrContent.substring(qrContent.lastIndexOf("|SIG=") + 5);
    }

    /** Parse the payload (everything before |SIG=) from a QR content string. */
    public String extractPayload(String qrContent) {
        if (qrContent == null) return null;
        int sigIdx = qrContent.indexOf("|SIG=");
        return sigIdx > 0 ? qrContent.substring(0, sigIdx) : qrContent;
    }
}
