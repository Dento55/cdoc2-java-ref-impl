package ee.cyber.cdoc2.container.recipients;

import com.google.flatbuffers.FlatBufferBuilder;
import ee.cyber.cdoc2.crypto.KeyLabelTools;
import ee.cyber.cdoc2.crypto.keymaterial.DecryptionKeyMaterial;
import ee.cyber.cdoc2.crypto.KekTools;
import ee.cyber.cdoc2.crypto.keymaterial.decrypt.SecretDecryptionKeyMaterial;
import ee.cyber.cdoc2.services.Services;

import java.security.GeneralSecurityException;
import java.util.Arrays;


public class SymmetricKeyRecipient extends Recipient {

    private final byte[] salt;

    public SymmetricKeyRecipient(byte[] salt, byte[] encFmk, String recipientLabel) {
        super(encFmk, recipientLabel);
        this.salt = salt.clone();
    }

    @Override
    public Object getRecipientId() {
        if (KeyLabelTools.isFormatted(recipientKeyLabel)) {
            return KeyLabelTools.extractKeyLabel(recipientKeyLabel);
        }
        return recipientKeyLabel;
    }

    public byte[] getSalt() {
        return salt;
    }

    @Override
    public byte[] deriveKek(DecryptionKeyMaterial keyMaterial, Services notUsed)
        throws GeneralSecurityException {
        if (keyMaterial instanceof SecretDecryptionKeyMaterial secretKeyMaterial) {
            return KekTools.deriveKekForSymmetricKey(this, secretKeyMaterial);
        }

        throw new GeneralSecurityException(
            "Unsupported key material type for recipient " + keyMaterial.getRecipientId()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SymmetricKeyRecipient that = (SymmetricKeyRecipient) o;
        return Arrays.equals(salt, that.salt);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(salt);
        return result;
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        return RecipientSerializer.serializeSymmetricKeyRecipient(this, builder);
    }

}
