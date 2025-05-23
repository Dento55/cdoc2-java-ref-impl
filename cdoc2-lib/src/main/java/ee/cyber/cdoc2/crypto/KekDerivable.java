package ee.cyber.cdoc2.crypto;

import ee.cyber.cdoc2.crypto.keymaterial.DecryptionKeyMaterial;
import ee.cyber.cdoc2.exceptions.CDocException;
import ee.cyber.cdoc2.services.Services;

import java.security.GeneralSecurityException;


/**
 * Classes that implement this interface, can derive KEK (key encryption key) using key material and key capsule client
 */
public interface KekDerivable {

    byte[] deriveKek(DecryptionKeyMaterial keyMaterial, Services services)
        throws GeneralSecurityException, CDocException;

}
