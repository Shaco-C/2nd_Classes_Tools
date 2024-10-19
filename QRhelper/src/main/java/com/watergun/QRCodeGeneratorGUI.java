package com.watergun;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class QRCodeGeneratorGUI extends JFrame {
    private JTextField signInOutIdField;
    private JTextField activityIdField;
    private JLabel qrCodeLabel;

    public QRCodeGeneratorGUI() {
        setTitle("二维码生成器");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // 输入signInOutId的文本框
        JLabel signInOutIdLabel = new JLabel("signInOutId: ");
        signInOutIdField = new JTextField(30);
        add(signInOutIdLabel);
        add(signInOutIdField);

        // 输入activityId的文本框
        JLabel activityIdLabel = new JLabel("activityId: ");
        activityIdField = new JTextField(30);
        add(activityIdLabel);
        add(activityIdField);

        // 生成二维码的按钮
        JButton generateButton = new JButton("生成二维码");
        generateButton.addActionListener(e -> generateQRCode());
        add(generateButton);

        // 显示二维码的区域
        qrCodeLabel = new JLabel();
        add(qrCodeLabel);

        setVisible(true);
    }

    private void generateQRCode() {
        String signInOutId = signInOutIdField.getText();
        String activityId = activityIdField.getText();

        // 获取当前日期，时间固定为23:59:59
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1); // 将年份加 1
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+08:00");
        calendar.setTimeZone(timeZone);

        // 将日期格式化为"Sun Apr 28 2024 23:59:59 GMT+0800 (中国标准时间)"
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT+0800 (中国标准时间)'", Locale.US);
        sdf.setTimeZone(timeZone);
        String formattedDate = sdf.format(calendar.getTime());

        // 对时间进行Base64编码，使用UTF-8字符集
        String encodedTime = Base64.getEncoder().encodeToString(formattedDate.getBytes(StandardCharsets.UTF_8));

        // 拼接URL
        String url = String.format("http://2ndclass.eqclub.cn/wechat/#/mjxy/stdnt/usercenter/scanningCheck?signInOutId=%s&activityId=%s&time=%s",
                signInOutId, activityId, encodedTime);

        // 生成二维码并在界面上显示
        try {
            BufferedImage qrImage = generateQRCodeImage(url);
            ImageIcon icon = new ImageIcon(qrImage);
            qrCodeLabel.setIcon(icon);
            qrCodeLabel.setText(""); // 清除之前的文字
        } catch (WriterException e) {
            e.printStackTrace();
            qrCodeLabel.setText("生成二维码失败");
        }
    }

    private BufferedImage generateQRCodeImage(String text) throws WriterException {
        int size = 300; // QR码的大小
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);

        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE); // 背景设为白色
        graphics.fillRect(0, 0, size, size);
        graphics.setColor(Color.BLACK); // 前景设为黑色

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRCodeGeneratorGUI::new);
    }
}
