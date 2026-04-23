# BlockChain

Some Tests and ideas about BlockChain and its uses. 

Just remember basics uses for Blockhain : 
  * Exchange Currency (CryptoCurrencies) ; 
  * Smart Contracts (validation, execution...) for example in supply chain / anti-counterfeiting / validation ; 
  * Property certification (NFT, certificates...) ;

## Aim of this repository

This repository is to be an example of uses and implementation of BlockChain(s). 

This a 'basic' example of a monetary transferts : 'Transactions' could be recoded as exchange of property certification, contracts (smart contracts, as automatic ones, or others)... 

## Notes

Notes [20240908]
  - On tests, after some transsaction, balance not valid as attempted (present before this note)
  - How to implement stak and validation ?
  - After how many validation stak is released ?

Notes [20240925]
  - Known bug "balance not valid as attempted" not checked systematically, noted with " XXX BUG " in tests assertions removed, how to correctly check balance anyway correctly ?! (need help)
  - Check the blockchain as a graph for some behaviors (possible attacks, known or not)
  - Began implementation of proof of stak (PoS) and it's use (with validators and default)

Notes [20250911]
 - reprise avec documentation plus générale et autres implémentations ; 
 - améliorations possibles : 
    - Pour PoW : Ajouter un ajustement dynamique de la difficulté.
    - Pour PoS : Implémenter une pénalité pour les validateurs malhonnêtes.
    - Commun : Ajouter des transactions signées et un système de portefeuille plus complet.
    - Tester : Exécuter le code avec des scénarios de fraude pour vérifier les pénalités et les signatures.
    - Optimiser : Ajouter un système de persistance (sauvegarde de la blockchain sur disque).
    - Étendre : Implémenter un réseau P2P pour une blockchain décentralisée.
    
## Rédiger documentation explicative de base sur ce modèle : 

### Proof of Work (PoW)

 - Fonctionnement :
    - Les mineurs résolvent un puzzle cryptographique (trouver un hash avec un certain nombre de zéros).
    - Le premier mineur à trouver la solution ajoute le bloc à la chaîne et reçoit une récompense.
 - Avantages :
    - Sécurité éprouvée (utilisée par Bitcoin).
 - Inconvénients :
    - Consommation d’énergie élevée.

### Proof of Stake (PoS)

 - Fonctionnement :
    - Les validateurs sont choisis en fonction de leur stake (nombre de tokens détenus).
    - Plus un validateur a de tokens, plus il a de chances d’être sélectionné pour valider un bloc.
 - Avantages :
    - Moins énergivore que le PoW.
    - Récompenses proportionnelles au stake.
 - Inconvénients :
    - Risque de centralisation (les plus riches validateurs dominent).
```
