package com.pharmacy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.entity.Medicine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {
    @Select("""
            <script>
            SELECT m.*,
                   (
                       SELECT GROUP_CONCAT(DISTINCT COALESCE(s.short_name, s.supplier_name) ORDER BY s.supplier_id SEPARATOR '、')
                       FROM stock st
                       LEFT JOIN supplier s ON st.supplier_id = s.supplier_id
                       WHERE st.med_id = m.med_id
                         AND st.supplier_id IS NOT NULL
                   ) AS supplier_name
            FROM medicine m
            <where>
                <if test="medName != null and medName != ''">
                    AND (m.med_name LIKE CONCAT('%', #{medName}, '%')
                         OR m.med_alias LIKE CONCAT('%', #{medName}, '%'))
                </if>
                <if test="medType != null and medType != ''">
                    AND m.med_type = #{medType}
                </if>
                <if test="status != null">
                    AND m.status = #{status}
                </if>
                <if test="status == null">
                    AND m.status = 1
                </if>
            </where>
            ORDER BY m.med_id ASC
            </script>
            """)
    Page<Medicine> selectMedicinePage(Page<Medicine> page,
                                      @Param("medName") String medName,
                                      @Param("medType") String medType,
                                      @Param("status") Integer status);

    @Select("""
            SELECT m.*,
                   (
                       SELECT GROUP_CONCAT(DISTINCT COALESCE(s.short_name, s.supplier_name) ORDER BY s.supplier_id SEPARATOR '、')
                       FROM stock st
                       LEFT JOIN supplier s ON st.supplier_id = s.supplier_id
                       WHERE st.med_id = m.med_id
                         AND st.supplier_id IS NOT NULL
                   ) AS supplier_name
            FROM medicine m
            WHERE m.med_id = #{id}
            """)
    Medicine selectMedicineById(@Param("id") Integer id);

    @Update("""
            <script>
            UPDATE medicine
            <set>
                <if test="medicine.medName != null">med_name = #{medicine.medName},</if>
                <if test="medicine.medAlias != null">med_alias = #{medicine.medAlias},</if>
                <if test="medicine.medType != null">med_type = #{medicine.medType},</if>
                <if test="medicine.spec != null">spec = #{medicine.spec},</if>
                <if test="medicine.unit != null">unit = #{medicine.unit},</if>
                <if test="medicine.dosageForm != null">dosage_form = #{medicine.dosageForm},</if>
                <if test="medicine.manufacturer != null">manufacturer = #{medicine.manufacturer},</if>
                <if test="medicine.approvalNo != null">approval_no = #{medicine.approvalNo},</if>
                <if test="medicine.retailPrice != null">retail_price = #{medicine.retailPrice},</if>
                <if test="medicine.purchasePrice != null">purchase_price = #{medicine.purchasePrice},</if>
                <if test="medicine.isRx != null">is_rx = #{medicine.isRx},</if>
                <if test="medicine.stockMin != null">stock_min = #{medicine.stockMin},</if>
                <if test="medicine.status != null">status = #{medicine.status},</if>
                <if test="medicine.remark != null">remark = #{medicine.remark},</if>
                update_time = NOW()
            </set>
            WHERE med_id = #{medicine.medId}
            </script>
            """)
    int updateMedicineById(@Param("medicine") Medicine medicine);
}
