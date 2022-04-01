package mage.cards.e;

import mage.abilities.Ability;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.ExileTargetForSourceEffect;
import mage.abilities.effects.common.counter.AddCountersTargetEffect;
import mage.cards.*;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;

import java.util.UUID;
import mage.abilities.effects.common.ReturnToBattlefieldUnderOwnerControlTargetEffect;

/**
 * @author LevelX2
 */
public final class EssenceFlux extends CardImpl {

    public EssenceFlux(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}");

        // Exile target creature you control, then return that card to the battlefield under its owner's control. If it's a Spirit, put a +1/+1 counter on it.
        this.getSpellAbility().addTarget(new TargetControlledCreaturePermanent());
        this.getSpellAbility().addEffect(new ExileTargetForSourceEffect());
        this.getSpellAbility().addEffect(new EssenceFluxEffect());
    }

    private EssenceFlux(final EssenceFlux card) {
        super(card);
    }

    @Override
    public EssenceFlux copy() {
        return new EssenceFlux(this);
    }
}

class EssenceFluxEffect extends ReturnToBattlefieldUnderOwnerControlTargetEffect {

    EssenceFluxEffect() {
        super(false, false);
        staticText = "return that card to the battlefield under its owner's control. If it's a Spirit, put a +1/+1 counter on it";
    }

    EssenceFluxEffect(final EssenceFluxEffect effect) {
        super(effect);
    }

    @Override
    public EssenceFluxEffect copy() {
        return new EssenceFluxEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean toReturn = super.apply(game, source);
        
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Cards cardsToBattlefield = new CardsImpl();
            for (UUID targetId : this.getTargetPointer().getTargets(game, source)) {
                UUID mainCardId = CardUtil.getMainCardId(game, targetId);
                cardsToBattlefield.add(mainCardId);
            }

            if (!cardsToBattlefield.isEmpty()) {
                for (UUID cardId : cardsToBattlefield) {
                    Permanent permanent = game.getPermanent(cardId);
                    if (permanent != null && permanent.hasSubtype(SubType.SPIRIT, game)) {
                        Effect effect = new AddCountersTargetEffect(CounterType.P1P1.createInstance());
                        effect.setTargetPointer(new FixedTarget(permanent, game));
                        return effect.apply(game, source);
                    }
                }
            }
            return toReturn;
        }
        return false;
    }
}
