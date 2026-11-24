package kasir.ui;

import kasir.data.ProductRepository;
import kasir.model.Product;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.function.Consumer;

/** Katalog produk: pencarian, filter kategori, dan grid kartu produk. */
public class ProductGridPanel extends RoundedPanel {

    private final ProductRepository repository;
    private final Consumer<Product> onPick;

    private final HintTextField searchField = new HintTextField("Cari produk atau kategori...");
    private final JPanel cardsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
    private final JPanel chipsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));
    private final JLabel countLabel = new JLabel();
    private final JScrollPane scroll;

    private String activeCategory = "Semua";

    public ProductGridPanel(ProductRepository repository, Consumer<Product> onPick) {
        super(new BorderLayout(0, 12), Theme.SURFACE, 16);
        this.repository = repository;
        this.onPick = onPick;

        setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new javax.swing.BoxLayout(header, javax.swing.BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("Produk");
        title.setFont(Theme.font(Font.BOLD, 16));
        title.setForeground(Theme.TEXT);
        countLabel.setFont(Theme.font(Font.PLAIN, 11));
        countLabel.setForeground(Theme.MUTED);
        titleRow.add(title, BorderLayout.WEST);
        titleRow.add(countLabel, BorderLayout.EAST);

        searchField.setPreferredSize(new Dimension(200, 38));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        chipsPanel.setOpaque(false);

        header.add(titleRow);
        header.add(javax.swing.Box.createVerticalStrut(10));
        header.add(searchField);
        header.add(javax.swing.Box.createVerticalStrut(10));
        header.add(chipsPanel);

        cardsPanel.setOpaque(false);

        scroll = Theme.scroll(cardsPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                rebuildCards();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                rebuildCards();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                rebuildCards();
            }
        });

        refresh();
    }

    public void refresh() {
        rebuildChips();
        rebuildCards();
    }

    public void focusSearch() {
        searchField.requestFocusInWindow();
        searchField.selectAll();
    }

    private void rebuildChips() {
        chipsPanel.removeAll();
        List<String> categories = new ArrayList<>();
        categories.add("Semua");
        for (String category : new TreeSet<>(collectCategories())) {
            categories.add(category);
        }
        if (!categories.contains(activeCategory)) {
            activeCategory = "Semua";
        }
        for (String category : categories) {
            boolean active = category.equals(activeCategory);
            FlatButton chip = new FlatButton(category,
                    active ? Theme.PRIMARY : Theme.SURFACE,
                    active ? java.awt.Color.WHITE : Theme.MUTED,
                    16);
            chip.setFont(Theme.font(Font.BOLD, 11));
            chip.setBorder(BorderFactory.createEmptyBorder(6, 13, 6, 13));
            chip.setLine(active ? Theme.PRIMARY : Theme.BORDER);
            chip.addActionListener(e -> {
                activeCategory = category;
                rebuildChips();
                rebuildCards();
            });
            chipsPanel.add(chip);
        }
        chipsPanel.revalidate();
        chipsPanel.repaint();
    }

    private List<String> collectCategories() {
        List<String> result = new ArrayList<>();
        for (Product product : repository.findAll()) {
            if (product.getCategory() != null && !product.getCategory().isBlank()) {
                result.add(product.getCategory());
            }
        }
        return result;
    }

    private void rebuildCards() {
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        List<Product> filtered = repository.findAll().stream()
                .filter(p -> activeCategory.equals("Semua") || p.getCategory().equalsIgnoreCase(activeCategory))
                .filter(p -> query.isEmpty()
                        || p.getName().toLowerCase(Locale.ROOT).contains(query)
                        || p.getCategory().toLowerCase(Locale.ROOT).contains(query))
                .sorted(Comparator.comparing(Product::getCategory).thenComparing(Product::getName))
                .toList();

        cardsPanel.removeAll();
        for (Product product : filtered) {
            cardsPanel.add(new ProductCard(product, onPick));
        }
        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("Produk tidak ditemukan", JLabel.CENTER);
            empty.setFont(Theme.BODY);
            empty.setForeground(Theme.MUTED);
            empty.setPreferredSize(new Dimension(420, 120));
            cardsPanel.add(empty);
        }
        countLabel.setText(filtered.size() + " produk");
        cardsPanel.revalidate();
        cardsPanel.repaint();
        javax.swing.SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(0));
    }
}
