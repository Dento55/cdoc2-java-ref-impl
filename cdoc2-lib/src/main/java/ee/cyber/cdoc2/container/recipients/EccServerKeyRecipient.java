package ee.cyber.cdoc2.container.recipients;

import com.google.flatbuffers.FlatBufferBuilder;
import ee.cyber.cdoc2.client.KeyCapsuleClientFactory;
import ee.cyber.cdoc2.exceptions.CDocException;
import ee.cyber.cdoc2.crypto.keymaterial.DecryptionKeyMaterial;
import ee.cyber.cdoc2.crypto.EllipticCurve;
import ee.cyber.cdoc2.crypto.KekTools;
import ee.cyber.cdoc2.crypto.keymaterial.decrypt.KeyPairDecryptionKeyMaterial;
import ee.cyber.cdoc2.fbs.recipients.EccKeyDetails;
import ee.cyber.cdoc2.services.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.security.interfaces.ECPublicKey;
import java.util.Objects;


/**
 * ECC recipient using EccKeyDetails. POJO of
 * {@link EccKeyDetails recipients.EccKeyDetails} in CDOC header.
 */
public class EccServerKeyRecipient extends EccRecipient implements ServerRecipient {
    private static final Logger log = LoggerFactory.getLogger(EccServerKeyRecipient.class);
    private final String keyServerId;
    private final String transactionId;

    public EccServerKeyRecipient(
        EllipticCurve eccCurve,
        ECPublicKey recipientPubKey,
        String keyServerId,
        String transactionId,
        byte[] encryptedFmk,
        String recipientPubKeyLabel
    ) {
        super(eccCurve, recipientPubKey, recipientPubKeyLabel, encryptedFmk);
        this.keyServerId = keyServerId;
        this.transactionId = transactionId;
    }

    @Override
    public String getKeyServerId() {
        return keyServerId;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EccServerKeyRecipient that = (EccServerKeyRecipient) o;
        return Objects.equals(keyServerId, that.keyServerId)
                && Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), keyServerId, transactionId);
    }

    @Override
    public byte[] deriveKek(DecryptionKeyMaterial keyMaterial, Services services)
        throws GeneralSecurityException, CDocException {

        if (services == null || !services.hasService(KeyCapsuleClientFactory.class)) {
            log.warn("Key capsule client is not configured");
        }

        if (keyMaterial instanceof KeyPairDecryptionKeyMaterial keyPairKeyMaterial
            && services != null && services.hasService(KeyCapsuleClientFactory.class)) {
            return KekTools.deriveKekForEccServer(
                this,
                keyPairKeyMaterial,
                services.get(KeyCapsuleClientFactory.class)
            );
        }

        throw new GeneralSecurityException(
            "Unsupported key material type for recipient " + keyMaterial.getRecipientId()
        );
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        return RecipientSerializer.serialize(this, builder);
    }

}
