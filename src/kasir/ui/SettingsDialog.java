package kasir.ui;

import kasir.data.DataException;
import kasir.data.Settings;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;

/** Dialog pengaturan toko, kasir, dan pajak. */
public class SettingsDialog extends JDialog {

    private final Settings settings;
    private final Runnable onSaved;

    private final JTextField storeField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField cashierField = new JTextField();
    private final JTextField footerField = new JTextField();
    private final JSpinner taxSpinner = new JSpinner(
            new SpinnerNumberModel(11.0, 0.0, 100.0, 0.5));

    private final JLabel errorLabel = new JLabel(" ");

    public SettingsDialog(Window owner, Settings settings, Runnable onSaved) {
        super(owner, "Pengaturan", ModalityType.APPLICATION_MODAL);
        this.settings = settings;
        this.onSaved = onSaved;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.BG);

        RoundedPanel root = new RoundedPanel(new BorderLayout(), Theme.SURFACE, 18);
        root.setBorder(new EmptyBorder(22, 24, 20, 24));
        setContentPane(root);

        storeField.setText(settings.getStoreName());
        addressField.setText(settings.getAddress());
        phoneField.setText(settings.getPhone());
        cashierField.setText(settings.getCashier());
        footerField.setText(settings.getFooter());
        taxSpinner.setValue(settings.getTaxRate());

        for (JTextField field : new JTextField[]{
                storeField, addressField, phoneField, cashierField, footerField}) {
            Theme.styleField(field);
            field.setPreferredSize(new Dimension(200, 38));
        }

        taxSpinner.setFont(Theme.BODY);
        taxSpinner.setPreferredSize(new Dimension(80, 34));
        if (taxSpinner.getEditor() instanceof JSpinner.DefaultEditor editor) {
            JTextField field = editor.getTextField();
            field.setFont(Theme.font(Font.BOLD, 13));
            field.setHorizontalAlignment(JTextField.RIGHT);
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        int row = 0;

        JLabel title = new JLabel("Pengaturan");
        title.setFont(Theme.font(Font.BOLD, 18));
        title.setForeground(Theme.TEXT);
        form.add(title, rowConstraints(row++, 0, 16));

        form.add(caption("Nama Toko"), rowConstraints(row++, 0, 4));
        form.add(storeField, rowConstraints(row++, 0, 10));

        form.add(caption("Alamat"), rowConstraints(row++, 0, 4));
        form.add(addressField, rowConstraints(row++, 0, 10));

        form.add(caption("Telepon"), rowConstraints(row++, 0, 4));
        form.add(phoneField, rowConstraints(row++, 0, 10));

        JPanel twoColumns = new JPanel(new GridLayout(1, 2, 12, 0));
        twoColumns.setOpaque(false);
        twoColumns.add(column("Nama Kasir", cashierField));

        JPanel taxWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        taxWrap.setOpaque(false);
        taxWrap.add(taxSpinner);
        twoColumns.add(column("PPN (%)", taxWrap));
        form.add(twoColumns, rowConstraints(row++, 0, 10));

        form.add(caption("Teks Penutup Struk"), rowConstraints(row++, 0, 4));
        form.add(footerField, rowConstraints(row++, 0, 8));

        errorLabel.setFont(Theme.font(Font.PLAIN, 11));
        errorLabel.setForeground(Theme.DANGER);
        form.add(errorLabel, rowConstraints(row++, 0, 0));

        GridBagConstraints glue = rowConstraints(row++, 0, 0);
        glue.weighty = 1.0;
        glue.fill = GridBagConstraints.BOTH;
        form.add(Box.createGlue(), glue);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        buttons.setOpaque(false);
        FlatButton save = new FlatButton("Simpan Pengaturan", Theme.PRIMARY, Color.WHITE, 10);
        save.setBorder(new EmptyBorder(9, 8, 9, 8));
        save.addActionListener(e -> save());
        FlatButton cancel = FlatButton.outline("Batal", Theme.MUTED, Theme.BORDER);
        cancel.setBorder(new EmptyBorder(9, 8, 9, 8));
        cancel.addActionListener(e -> dispose());
        buttons.add(save);
        buttons.add(cancel);
        form.add(buttons, rowConstraints(row, 0, 0));

        root.add(form, BorderLayout.CENTER);
        setSize(470, 560);
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(save);
    }

    private static GridBagConstraints rowConstraints(int row, int top, int bottom) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(top, 0, bottom, 0);
        return constraints;
    }

    private JPanel column(String caption, java.awt.Component field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        panel.add(caption(caption), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.font(Font.BOLD, 11));
        label.setForeground(Theme.MUTED);
        return label;
    }

    private void save() {
        if (storeField.getText().trim().isEmpty()) {
            errorLabel.setText("Nama toko wajib diisi.");
            return;
        }
        if (cashierField.getText().trim().isEmpty()) {
            errorLabel.setText("Nama kasir wajib diisi.");
            return;
        }
        settings.setStoreName(storeField.getText().trim());
        settings.setAddress(addressField.getText().trim());
        settings.setPhone(phoneField.getText().trim());
        settings.setCashier(cashierField.getText().trim());
        settings.setFooter(footerField.getText().trim());
        settings.setTaxRate(((Number) taxSpinner.getValue()).doubleValue());
        try {
            settings.save();
        } catch (DataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (onSaved != null) {
            onSaved.run();
        }
        dispose();
    }
}
