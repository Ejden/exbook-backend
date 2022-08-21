package pl.exbook.exbook.order.domain

import org.springframework.stereotype.Service
import pl.exbook.exbook.order.domain.Order.OrderStatus
import pl.exbook.exbook.shipping.domain.Shipping
import pl.exbook.exbook.user.domain.User

@Service
class OrderStatusChangeValidator {
    fun validateStatusChange(command: OrderStatusChangeCommand, user: User, order: Order) = when (command.newStatus) {
        OrderStatus.NEW, OrderStatus.WAITING_FOR_ACCEPT -> throw IllegalStatusChangeException()
        OrderStatus.SENT -> validateStatusChangeToSent(user, order)
        OrderStatus.DELIVERED -> validateStatusChangeToDelivered(user, order)
        OrderStatus.DECLINED -> validateStatusChangeToDeclined(user, order)
        OrderStatus.ACCEPTED -> throw IllegalStatusChangeException(
            "Cannot change status to accepted. Use new API for this action instead."
        )
        OrderStatus.RETURN_DELIVERED -> validateStatusChangeToReturnDelivered(user, order)
        OrderStatus.RETURN_IN_PROGRESS -> validateStatusChangeToReturnInProgress(user, order)
        OrderStatus.CANCELED -> validateStatusChangeToCanceled(user, order)
    }

    fun validateStatusChangeToAccepted(command: AcceptExchangeCommand, user: User, order: Order, shipping: Shipping) {
        if (user.id != order.seller.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'accepted'")
        }
        if (!order.canExchangeBeAccepted) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'accepted'")
        }
        if ((command.address == null && command.pickupPoint == null) || (command.address != null && command.pickupPoint != null)) {
            throw IllegalStatusChangeException("Seller ${user.id} tried to change status to 'accepted' providing non specific address")
        }
    }

    private fun validateStatusChangeToSent(user: User, order: Order) {
        if (user.id != order.seller.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'sent'")
        }
        if (!order.canBeMarkedAsSent) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'sent'")
        }
    }

    private fun validateStatusChangeToDelivered(user: User, order: Order) {
        if (user.id != order.buyer.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'delivered'")
        }
        if (!order.canBeMarkedAsDelivered) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'delivered'")
        }
    }

    private fun validateStatusChangeToDeclined(user: User, order: Order) {
        if (user.id != order.seller.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'declined'")
        }
        if (!order.canExchangeBeDismissed) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'declined'")
        }
    }

    private fun validateStatusChangeToReturnInProgress(user: User, order: Order) {
        if (user.id != order.buyer.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'return in progress'")
        }
        if (!order.canBeReturned) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'return in progress'")
        }
    }

    private fun validateStatusChangeToReturnDelivered(user: User, order: Order) {
        if (user.id != order.seller.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'return delivered'")
        }
        if (!order.canBeMarkedAsReturnDelivered) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'return delivered'")
        }
    }

    private fun validateStatusChangeToCanceled(user: User, order: Order) {
        if (user.id != order.seller.id && user.id != order.buyer.id) {
            throw IllegalStatusChangeException("User ${user.id} cannot change order ${order.id} status to 'canceled'")
        }
        if (!order.canBeCancelled) {
            throw IllegalStatusChangeException("Order ${order.id} status cannot be changed to 'canceled'")
        }
    }
}
