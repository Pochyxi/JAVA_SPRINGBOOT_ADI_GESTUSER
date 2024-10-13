package com.adi.gestuser.exception;

import lombok.Getter;

@Getter
public class ErrorCodeList {
    //RISORSA NON TROVATA
    public static final String NF404 = "NF404";

    //min = 6, max = 50
    public static final String SIZEERROR = "SIZEERROR";

    // @Mail
    public static final String FORMATERROR = "FORMATERROR";

    //USERNAME O PASS O EMAIL ERRATE
    public static final String BADCREDENTIALS = "BADCREDENTIALS";

    //Al MOMENTO DELLA REGISTRAZIONE SE QUESTO USERNAME E' GIA UTILIZZATO
    public static final String  EXISTINGUSERNAME = "EXISTINGUSERNAME";

    //AL MOMENTO DELLA REGISTRAZIONE SE QUESTA MAIL E' GIA UTILIZZATA
    public static final String EXISTINGEMAIL = "EXISTINGEMAIL";

    // AL MOMENTO DELLA REGISTRAZIONE SE LE PASS NON CORRISPONDONO
    public static final String NOTSAMEPASSWORDS = "NOTSAMEPASSWORDS";

    // AL MOMENTO DELLA REGISTRAZIONE SE L'UTENTE NON E' ABILITATO
    public static final String NOTUSERENABLED = "NOTUSERENABLED";

    // AL MOMENTO DELLA LOGIN SE LA PASS E' TEMPORANEA
    public static final String TEMPORARYPASSWORD = "TEMPORARYPASSWORD";

    // AL CAMBIO PASSWORD TUTTI I VECCHI TOKEN DIVENTANO OBSOLETI
    public static final String TOKENOBSOLETE = "TOKENOBSOLETE";

    // NON SI PUO' AVERE PIU' DI UNA CONFIMATION PER UN UTENTE
    public static final String EXISTINGCONFIRMATION = "EXISTINGCONFIRMATION";

    // DEVE ESSERE PER FORZA TUTTO IN UPPERCASE
    public static final String UPPERCASEERROR = "UPPERCASEERROR";

    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    ;
}
