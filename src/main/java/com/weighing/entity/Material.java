package com.weighing.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Entity
@Data
@Table(name="material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long materialId;   //id
    @Column(unique = true, nullable = false)//物资编号
    private String code;
    private String name;  //物资名称
    private String unit;  //物资单位
}
