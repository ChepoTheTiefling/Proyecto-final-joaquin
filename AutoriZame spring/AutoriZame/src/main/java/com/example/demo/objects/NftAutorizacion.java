package com.example.demo.objects;

import jakarta.persistence.*;

@Entity
@Table(name = "nft_autorizaciones")
public class NftAutorizacion {

    public enum Estado {
        ACTIVO,
        QUEMADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    private int pedidoId;
    private String ownerAddress;
    private String metadataUri;
    private String cidIpfs;
    private String metadataJson;
    private String codigoNumerico;
    private String idAutorizadoHash;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVO;

    private Long chainTokenId;
    private String mintTxHash;
    private String transferTxHash;
    private String burnTxHash;

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getMetadataUri() {
        return metadataUri;
    }

    public void setMetadataUri(String metadataUri) {
        this.metadataUri = metadataUri;
    }

    public String getCidIpfs() {
        return cidIpfs;
    }

    public void setCidIpfs(String cidIpfs) {
        this.cidIpfs = cidIpfs;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public String getCodigoNumerico() {
        return codigoNumerico;
    }

    public void setCodigoNumerico(String codigoNumerico) {
        this.codigoNumerico = codigoNumerico;
    }

    public String getIdAutorizadoHash() {
        return idAutorizadoHash;
    }

    public void setIdAutorizadoHash(String idAutorizadoHash) {
        this.idAutorizadoHash = idAutorizadoHash;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Long getChainTokenId() {
        return chainTokenId;
    }

    public void setChainTokenId(Long chainTokenId) {
        this.chainTokenId = chainTokenId;
    }

    public String getMintTxHash() {
        return mintTxHash;
    }

    public void setMintTxHash(String mintTxHash) {
        this.mintTxHash = mintTxHash;
    }

    public String getTransferTxHash() {
        return transferTxHash;
    }

    public void setTransferTxHash(String transferTxHash) {
        this.transferTxHash = transferTxHash;
    }

    public String getBurnTxHash() {
        return burnTxHash;
    }

    public void setBurnTxHash(String burnTxHash) {
        this.burnTxHash = burnTxHash;
    }
}
