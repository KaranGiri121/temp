part of 'pagination_bloc.dart';

@immutable
sealed class PaginationState {}

final class PaginationInitial extends PaginationState {}

final class PaginationLoading extends PaginationState{}

final class PaginationLoaded extends PaginationState{}