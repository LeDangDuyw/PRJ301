package dao;

import java.sql.*;
import java.util.*;
import model.Product;

public class ProductDAO extends DBContext {

    // ── BASE SQL join Categories + Brands ────────────────────────────────────
    private static final String BASE_SQL =
        "SELECT p.*, c.name AS categoryName, b.name AS brandName " +
        "FROM Products p " +
        "LEFT JOIN Categories c ON p.categoryId = c.id " +
        "LEFT JOIN Brands b ON p.brandId = b.id ";

    // ── Map ResultSet → Product ───────────────────────────────────────────────
    private Product extractProduct(ResultSet rs) throws Exception {
        Product p = new Product(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("slug"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("stock"),
            rs.getInt("sold"),
            rs.getString("image"),
            rs.getDouble("discount"),
            rs.getInt("warranty"),
            rs.getBoolean("isFeatured"),
            rs.getBoolean("status"),
            rs.getTimestamp("createdDate"),
            rs.getInt("categoryId"),
            rs.getInt("brandId")
        );
        try { p.setCategoryName(rs.getString("categoryName")); } catch (Exception ignored) {}
        try { p.setBrandName(rs.getString("brandName")); }     catch (Exception ignored) {}
        return p;
    }

    // ── Helper chạy query trả List<Product> ──────────────────────────────────
    private List<Product> query(String sql, Object... params) {
        List<Product> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(extractProduct(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CODE GỐC – ĐÃ SỬA dùng BASE_SQL có JOIN
    // ════════════════════════════════════════════════════════════════════════

    // ✅ SỬA: thêm JOIN → brandName, categoryName không còn null
    public List<Product> getAll() {
        return query(BASE_SQL + "WHERE p.status=1 ORDER BY p.createdDate DESC");
    }

    // ✅ SỬA: thêm JOIN → brandName, categoryName không còn null
    public Product getById(int id) {
        String sql = BASE_SQL + "WHERE p.id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractProduct(rs);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  THÊM MỚI – dùng cho trang Home
    // ════════════════════════════════════════════════════════════════════════

    /** Sản phẩm nổi bật (isFeatured = 1) */
    public List<Product> getFeaturedProducts(int limit) {
        return query(BASE_SQL +
            "WHERE p.status=1 AND p.isFeatured=1 " +
            "ORDER BY p.sold DESC " +
            "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY", limit);
    }

    /** Flash Sale – discount > 0, sắp xếp giảm giá cao nhất */
    public List<Product> getFlashSaleProducts(int limit) {
        return query(BASE_SQL +
            "WHERE p.status=1 AND p.discount > 0 " +
            "ORDER BY p.discount DESC " +
            "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY", limit);
    }

    /** Hàng mới về */
    public List<Product> getNewProducts(int limit) {
        return query(BASE_SQL +
            "WHERE p.status=1 " +
            "ORDER BY p.createdDate DESC " +
            "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY", limit);
    }

    /** Bán chạy nhất */
    public List<Product> getBestSellerProducts(int limit) {
        return query(BASE_SQL +
            "WHERE p.status=1 " +
            "ORDER BY p.sold DESC " +
            "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY", limit);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  THÊM MỚI – dùng cho trang Danh sách sản phẩm (filter + sort + paging)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Lấy sản phẩm có filter / sort / phân trang
     * @param keyword     null = bỏ qua
     * @param categoryId  0 = tất cả
     * @param brandId     0 = tất cả
     * @param minPrice    -1 = bỏ qua
     * @param maxPrice    -1 = bỏ qua
     * @param sort        "price_asc" | "price_desc" | "new" | "sold" | "discount" | ""
     * @param page        từ 1
     * @param pageSize    số SP mỗi trang
     */
    public List<Product> getProducts(String keyword, int categoryId, int brandId,
                                     double minPrice, double maxPrice,
                                     String sort, int page, int pageSize) {
        StringBuilder sb = new StringBuilder(BASE_SQL + "WHERE p.status=1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sb.append("AND (p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ?) ");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        if (categoryId > 0) { sb.append("AND p.categoryId=? "); params.add(categoryId); }
        if (brandId    > 0) { sb.append("AND p.brandId=? ");    params.add(brandId); }
        if (minPrice   >= 0) { sb.append("AND p.price*(1-ISNULL(p.discount,0)/100.0) >= ? "); params.add(minPrice); }
        if (maxPrice   >= 0) { sb.append("AND p.price*(1-ISNULL(p.discount,0)/100.0) <= ? "); params.add(maxPrice); }

        switch (sort == null ? "" : sort) {
            case "price_asc":  sb.append("ORDER BY p.price*(1-ISNULL(p.discount,0)/100.0) ASC ");  break;
            case "price_desc": sb.append("ORDER BY p.price*(1-ISNULL(p.discount,0)/100.0) DESC "); break;
            case "new":        sb.append("ORDER BY p.createdDate DESC "); break;
            case "sold":       sb.append("ORDER BY p.sold DESC ");        break;
            case "discount":   sb.append("ORDER BY p.discount DESC ");    break;
            default:           sb.append("ORDER BY p.isFeatured DESC, p.sold DESC "); break;
        }

        sb.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        return query(sb.toString(), params.toArray());
    }

    /** Đếm tổng số SP khớp filter (dùng cho phân trang) */
   
   public int countProducts(String keyword, int categoryId, int brandId,
                         double minPrice, double maxPrice) {
    StringBuilder sb = new StringBuilder(
        "SELECT COUNT(*) FROM Products p " +
        "LEFT JOIN Categories c ON p.categoryId=c.id " +
        "LEFT JOIN Brands b ON p.brandId=b.id " +
        "WHERE p.status=1 ");
    List<Object> params = new ArrayList<>();

    if (keyword != null && !keyword.trim().isEmpty()) {
        sb.append("AND (p.name LIKE ? OR b.name LIKE ? OR c.name LIKE ?) ");
        String kw = "%" + keyword.trim() + "%";
        params.add(kw); params.add(kw); params.add(kw);
    }
    if (categoryId > 0) { sb.append("AND p.categoryId=? "); params.add(categoryId); }
    if (brandId    > 0) { sb.append("AND p.brandId=? ");    params.add(brandId); }
    if (minPrice   >= 0) { sb.append("AND p.price*(1-ISNULL(p.discount,0)/100.0) >= ? "); params.add(minPrice); }
    if (maxPrice   >= 0) { sb.append("AND p.price*(1-ISNULL(p.discount,0)/100.0) <= ? "); params.add(maxPrice); }

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sb.toString())) {
        for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

     /**
     * Lấy sản phẩm tương tự (cùng category, loại trừ sp hiện tại)
     */
    public List<Product> getRelatedProducts(int categoryId, int currentProductId, int limit) {
        return query(BASE_SQL +
            "WHERE p.status=1 AND p.categoryId=? AND p.id!=? " +
            "ORDER BY p.createdDate DESC " +
            "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY",
            categoryId, currentProductId, limit);
    }

}