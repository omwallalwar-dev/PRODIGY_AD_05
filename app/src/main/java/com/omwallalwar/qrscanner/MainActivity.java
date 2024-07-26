package com.omwallalwar.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button scanQRButton;
    private TextView scanResultText;
    private Button copyButton;

    private final ActivityResultLauncher<Intent> qrScannerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                    if (intentResult != null) {
                        if (intentResult.getContents() == null) {
                            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                        } else {
                            handleScanResult(intentResult.getContents());
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanQRButton = findViewById(R.id.scan_qr_button);
        scanResultText = findViewById(R.id.scan_result_text);
        copyButton = findViewById(R.id.copy_button);

        scanQRButton.setOnClickListener(view -> startQRScanner());
        copyButton.setOnClickListener(view -> copyToClipboard());
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        qrScannerLauncher.launch(integrator.createScanIntent());
    }

    private void handleScanResult(String scanContent) {
        scanResultText.setText(scanContent);
        if (scanContent.startsWith("http://") || scanContent.startsWith("https://")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanContent));
            startActivity(browserIntent);
        } else {
            // Handle other types of QR code content if necessary
            Toast.makeText(this, "Scanned content: " + scanContent, Toast.LENGTH_LONG).show();
        }
    }

    private void copyToClipboard() {
        String textToCopy = scanResultText.getText().toString();
        if (!textToCopy.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("QR Code Content", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing to copy", Toast.LENGTH_SHORT).show();
        }
    }
}
